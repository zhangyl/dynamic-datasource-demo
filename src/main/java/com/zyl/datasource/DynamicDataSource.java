package com.zyl.datasource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.google.common.collect.ArrayListMultimap;
import com.zyl.bean.DataSourceDO;
import com.zyl.datasource.DataSourceBean.DataSourceBeanBuilder;
import com.zyl.mapper.DataSourceMapper;

/**
 */
public class DynamicDataSource extends AbstractRoutingDataSource implements ApplicationContextAware {
	
	final Logger log = LoggerFactory.getLogger(DynamicDataSource.class);
	
	private final AtomicInteger counter = new AtomicInteger(0);

	private ApplicationContext applicationContext;
	
	/**
	 * 主库
	 */
	Map<Object, DataSource> resolvedDataSources;
	/**
	 * 默认库
	 */
	DataSource resolvedDefaultDataSource;
	/**
	 * 备库
	 */
	ArrayListMultimap<Object, DataSource> slaveDataSourceBeanMap = ArrayListMultimap.create();
	
	boolean lenientFallback = true;
	
	public void initDataSouces() {

		//初始化自定义
		Map<Object, Object> targetDataSources = getTargetDataSources();
		resolvedDataSources = getResolvedDataSources();
		resolvedDefaultDataSource = getResolvedDefaultDataSource();
		
		DataSourceMapper dataSourceMapper = applicationContext.getBean(DataSourceMapper.class);
		List<DataSourceDO> dataSourceDOList = dataSourceMapper.getAllDataSources();
		for (DataSourceDO dataSourceDO : dataSourceDOList) {
			DataSourceBeanBuilder builder = new DataSourceBeanBuilder(dataSourceDO.getDatasourceName(),
					dataSourceDO.getDatabaseIp(), dataSourceDO.getDatabasePort(), dataSourceDO.getDatabaseName(),
					dataSourceDO.getUsername(), dataSourceDO.getPassword(), dataSourceDO.getDatabaseUrlExtraParam());
			
			DataSourceBean dataSourceBean = new DataSourceBean(builder);
			/*
			 *  先在spring容器中创建该数据源bean
			*/
			Object dataSource = createDataSource(dataSourceBean);
			//在创建后的bean,放入到targetDataSources Map中
			targetDataSources.put(dataSourceBean.getBeanName(), dataSource);
		}
        
		targetDataSources.forEach((key, value) -> {
			Object lookupKey = resolveSpecifiedLookupKey(key);
			DataSource dataSource = resolveSpecifiedDataSource(value);
			resolvedDataSources.put(lookupKey, dataSource);
			
		});
		
	}
	/**
	 * 连接数据源前,调用该方法
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		//1.获取手动设置的数据源参数DataSourceBean
		String dataSourceKey = DataSourceContext.getDataSource();
		return dataSourceKey;

	}
	
	@Override
	protected DataSource determineTargetDataSource() {
		Object lookupKey = determineCurrentLookupKey();
		boolean selectStament = SelectStatmentContext.isSelectStatment();
		//如果有备库，查询优先选择备库，读写分离
		if (selectStament) {
			List<DataSource> slaveDataSource = slaveDataSourceBeanMap.get(lookupKey);
			if(slaveDataSource != null && slaveDataSource.size()>0) {
				int idx = slaveDataSource.size();
				if(idx == 1) {
					return slaveDataSource.get(idx);
				}
				/**
				 * 读写分离读slave库策略：round robin 
				 */
				idx = counter.incrementAndGet() % slaveDataSource.size();
				return slaveDataSource.get(idx);
			}
		}
		//主库
		DataSource dataSource = resolvedDataSources.get(lookupKey);
		if (dataSource == null && (this.lenientFallback || lookupKey == null)) {
			//默认库
			dataSource = resolvedDefaultDataSource;
		}
		if (dataSource == null) {
			throw new IllegalStateException("Cannot determine target DataSource for lookup key [" + lookupKey + "]");
		}
		return dataSource;
	}
	/**
	 * 根据数据源信息在spring中创建bean,并返回
	 * @param dataSourceBean 数据源信息
	 * @return
	 * @throws IllegalAccessException
	 */
	public Object createDataSource(DataSourceBean dataSourceBean) {
		//1.将applicationContext转化为ConfigurableApplicationContext
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
		//2.获取bean工厂并转换为DefaultListableBeanFactory
		DefaultListableBeanFactory beanFactory =  (DefaultListableBeanFactory) context.getBeanFactory();
		/*
		 * 3.本文用的是DruidDataSource,所有在这里我们获取的是该bean的BeanDefinitionBuilder,
		 * 通过BeanDefinitionBuilder来创建bean定义
		 */
		BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DruidDataSource.class);
		/*
		 * 4.获取DataSourceBean里的属性和对应值,并将其交给BeanDefinitionBuilder创建bean的定义
		 */
		Map<String, Object> propertyKeyValues = getPropertyKeyValues(DataSourceBean.class, dataSourceBean);
		for(Map.Entry<String,Object> entry : propertyKeyValues.entrySet()) {
			beanDefinitionBuilder.addPropertyValue(entry.getKey(), entry.getValue());
		}
		//5.bean定义好以后,将其交给beanFactory注册成bean对象，由spring容器管理
		beanFactory.registerBeanDefinition(dataSourceBean.getBeanName(), beanDefinitionBuilder.getBeanDefinition());
		//6.最后获取步骤5生成的bean,并将其返回
		return context.getBean(dataSourceBean.getBeanName());
	}
	//获取类属性和对应的值,放入Map中
	private <T> Map<String, Object> getPropertyKeyValues(Class<T> clazz, Object object){
       Field[] fields = clazz.getDeclaredFields();
       Map<String,Object> map = new HashMap<>();
       for (Field field : fields) {
    	   field.setAccessible(true);
    	   try {
			map.put(field.getName(), field.get(object));
		} catch (Exception ingore) {
			
		}
       }
       map.remove("beanName");
       return map;
    }
    //通过反射获取AbstractRoutingDataSource的targetDataSources属性
	@SuppressWarnings("unchecked")
	public Map<Object, Object> getTargetDataSources() {
		try {
			Field field = AbstractRoutingDataSource.class.getDeclaredField("targetDataSources");
			field.setAccessible(true);
			return (Map<Object, Object>) field.get(this);
		} catch (Exception ignore) {
			
		}
		return null;
		
	}
	@SuppressWarnings("unchecked")
	public Map<Object, DataSource> getResolvedDataSources() {
		
		try {
			Field field = AbstractRoutingDataSource.class.getDeclaredField("resolvedDataSources");
			field.setAccessible(true);
			return (Map<Object, DataSource>) field.get(this);
		} catch (Exception ignore) {
			
		}
		return null;
	}
	
	public DataSource getResolvedDefaultDataSource() {
		
		try {
			Field field = AbstractRoutingDataSource.class.getDeclaredField("resolvedDefaultDataSource");
			field.setAccessible(true);
			return (DataSource) field.get(this);
		} catch (Exception ignore) {
			
		}
		return null;
	}
	
	public boolean getLenientFallback() {
		
		try {
			Field field = AbstractRoutingDataSource.class.getDeclaredField("lenientFallback");
			field.setAccessible(true);
			return (Boolean) field.get(this);
		} catch (Exception ignore) {
			
		}
		return true;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
	}
}
