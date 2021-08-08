package com.zyl;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ImportResource;

import com.zyl.datasource.DynamicDataSource;

@SpringBootApplication
@ImportResource(locations={"classpath:spring-dynamic-mybatis.xml"})
public class Application  {
    public static void main(String[] args) {
    	ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
    	DynamicDataSource dynamicDataSource = context.getBean(DynamicDataSource.class);
    	dynamicDataSource.initDynamicDataSouces();
    }

}
