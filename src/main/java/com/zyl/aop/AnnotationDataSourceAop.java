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
	
    @Pointcut("@annotation(com.zyl.annotation.DynamicRoutingDataSource)")
    public void $$mybatisMapperPointCut$$(){

    }
    
	@Around(value = "$$mybatisMapperPointCut$$()")
	public Object aroundOpt(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		//当前dataSource
		String currentDataSource = DataSourceContext.getDataSource();

		DynamicRoutingDataSource annotation = null;
        MethodInvocation methodInvocation = ExposeInvocationInterceptor.currentInvocation();
        if (methodInvocation != null){
        	Method method = methodInvocation.getMethod();
        	annotation = method.getAnnotation(DynamicRoutingDataSource.class);
        }

		Object result = null;
		try {
			//临时切换到DynamicRoutingDataSource注解指定 DataSource
			if(annotation != null && annotation.value() != null) {
				DataSourceContext.setDataSource(annotation.value());
				log.debug("切到" + annotation.value() + "数据库");
			}
			result = proceedingJoinPoint.proceed();
		} finally {
			//有过临时切换数据源，需要恢复切换前
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
		return result;
	}

}
