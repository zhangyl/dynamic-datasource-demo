package com.zyl.aop;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.stereotype.Component;

import com.zyl.annotation.DynamicRoutingDataSource;
import com.zyl.datasource.DataSourceContext;


/**
 * 拦截器，如果通过 @DynamicRoutingDataSource 注解在方法上(包括mybatis的Mapper方法)上指定动态路由某一个数据源，
 * 则对应的方法内所有sql路由到注解指定的数据源，如果方法内中的方法继续使用此注解，按照注解就近原则路由。
 */
@Aspect
@Component
public class AnnotationDataSourceAop {

	private final Logger log = LoggerFactory.getLogger(AnnotationDataSourceAop.class);
	
    @Pointcut("@within(com.zyl.annotation.DynamicRoutingDataSource) || @annotation(com.zyl.annotation.DynamicRoutingDataSource)")
    public void $$mybatisMapperPointCut$$(){

    }
    /**
     * 优先使用方法注解数据源路由，如果方法上数据源注解不存在，再获取类上面的数据注解
     */
	@Around(value = "$$mybatisMapperPointCut$$()")
	public Object aroundOpt(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		//当前dataSource
		String currentDataSource = DataSourceContext.getDataSource();

		DynamicRoutingDataSource annotation = null;
        MethodInvocation methodInvocation = ExposeInvocationInterceptor.currentInvocation();
        if (methodInvocation != null){
        	Method method = methodInvocation.getMethod();
        	//方法注解
        	annotation = method.getAnnotation(DynamicRoutingDataSource.class);
            //class注解
            if(annotation == null) {
    	        Class<?> clazz = method.getDeclaringClass();
    	        annotation = clazz.getAnnotation(DynamicRoutingDataSource.class);
            }
        }
		try {
			//切换到DynamicRoutingDataSource注解指定 DataSource
			if(annotation != null && annotation.value() != null) {
				DataSourceContext.setDataSource(annotation.value());
				log.debug("切到" + annotation.value() + "数据库");
			}
			return proceedingJoinPoint.proceed();
		} finally {
			//有过切换数据源，需要恢复到切换前的数据源
			if(annotation != null && annotation.value() != null) {
				//切换前为空，等于恢复到默认
				if(currentDataSource == null) {
					DataSourceContext.toDefault();
					log.debug("恢复到默认数据库");
				} else {
					DataSourceContext.setDataSource(currentDataSource);
					log.debug("恢复到" + currentDataSource + "数据库");
				}
				
			}
		} 
	}

}
