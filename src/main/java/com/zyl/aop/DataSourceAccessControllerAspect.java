package com.zyl.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.zyl.datasource.DataSourceContext;
import com.zyl.datasource.DynamicDataSource;


@Component
@Aspect
public class DataSourceAccessControllerAspect {
	final Logger log = LoggerFactory.getLogger(DataSourceAccessControllerAspect.class);
	@Pointcut("execution(* com.zyl.controller..*Controller*.*(..))")
    public void controllerPointcut(){

    }

    @Around(value="controllerPointcut")
    public Object handleLimit(ProceedingJoinPoint joinPoint) throws Throwable {
    	try {
    		//FIXME zyl: 从访问上下文中获取routerCode
    		String routerCode = DynamicDataSource.routerCodeMasterMap.get("");
    		DataSourceContext.setDataSourceRouterCode(routerCode);
    		return joinPoint.proceed();
    	} finally {
    		
    	}

    }

}
