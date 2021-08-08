package com.zyl.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyl.bean.Cost;
import com.zyl.datasource.DataSourceContext;
import com.zyl.service.CostService;

@RestController
public class TestController {
	private final Logger log = LoggerFactory.getLogger(TestController.class);
	
	@Autowired
	CostService costService;
	
	@PostMapping("testAnnotationSlave")
	public String testSelect(HttpServletRequest request) {
        
        {
        	//默认数据库,不建表会报错
	        Cost cost = new Cost();
	        cost.setMoney(102);
	        cost.setEntCode("2");
	        costService.insert(cost);
        }
		return "hello,world";
	}
	@PostMapping("testAnnotationSlave")
	public String testSelectAnnotationSlave(HttpServletRequest request) {
		{
			//走默认数据库查询，如果主库配置了备库
			Cost c = costService.selectByPrimaryKey(1);
			log.info("-------------------c={}", c);
		}
		
		return "OK";
	}
	/**
	 * 强制路由router_code = multi_1_master的数据源
	 * @param request
	 * @return
	 */
	@PostMapping("testAnnotationDataSourceMaster")
	public String testSelectAnnotationDataSourceMaster(HttpServletRequest request) {
	       {
	        	DataSourceContext.setDataSourceRouterCode("multi_1_master");
	        	Cost c = costService.selectByPrimaryKey(1);
	        	log.info("-------------------c={}", c);
	        	DataSourceContext.toDefault();
	        }
	       return "OK";
	}
	@PostMapping("testAnnotationDataSourceMasterInsert")
	public String testSelectAnnotationDataSourceMasterInsert(HttpServletRequest request) {
        {
        	//----test 路由到数据源二
	        DataSourceContext.setDataSourceRouterCode("multi_1_master");
	        Cost cost = new Cost();
	        cost.setMoney(101);
	        cost.setEntCode("1");
	        costService.insert(cost);
	        DataSourceContext.toDefault();
        }
		return "OK";
	}
	@PostMapping("testAnnotationDataSourceMaster2Insert")
	public String testAnnotationDataSourceMaster2Insert() {
        {
        	//----test 路由到数据源一
        	DataSourceContext.setDataSourceRouterCode("multi_2_master");
	        Cost cost = new Cost();
	        cost.setMoney(102);
	        cost.setEntCode("2");
	        costService.insert(cost);
	        DataSourceContext.toDefault();
        }
        return "OK";
	}
	@PostMapping("testAnnotationDataSourceMaster2InsertToMaster1")
	public String testAnnotationDataSourceMaster2InsertToMaster1() {
        {
        	//----test 路由到主键指定的数据源.此测试指定数据源multi_1
        	DataSourceContext.setDataSourceRouterCode("multi_2_master");
        	Cost cost = new Cost();
        	cost.setMoney(200);
        	cost.setEntCode("2");
        	costService.insert2(cost);
        	DataSourceContext.toDefault();
        }
        return "OK";
	}
	
}
