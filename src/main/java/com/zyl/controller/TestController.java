package com.zyl.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zyl.bean.Cost;
import com.zyl.bean.DataSourceDO;
import com.zyl.datasource.DataSourceContext;
import com.zyl.mapper.DataSourceMapper;
import com.zyl.service.CostService;

@RestController
public class TestController {
	private final Logger log = LoggerFactory.getLogger(TestController.class);
	
    @Autowired
    DataSourceMapper dataSourceMapper;
	@Autowired
	CostService costService;

	@PostMapping("test")
	public String test(HttpServletRequest request) {
		//测试公共数据源查询
        List<DataSourceDO> dataSourceDOList = dataSourceMapper.getAllDataSources();
        log.info("-----------------------dataSourceDOList.size() == {}", dataSourceDOList.size());
        {
        	DataSourceContext.setDataSourceRouterCode("multi_1");
        	Cost c = costService.selectByPrimaryKey(1);
        	log.info("-------------------c={}", c);
        	DataSourceContext.toDefault();
        }
        {
        	//----test 路由到数据源二
	        DataSourceContext.setDataSourceRouterCode("multi_1");
	        Cost cost = new Cost();
	        cost.setMoney(101);
	        cost.setEntCode("1");
	        costService.insert(cost);
	        DataSourceContext.toDefault();
        }
        {
        	//----test 路由到数据源一
        	DataSourceContext.setDataSourceRouterCode("multi_2");
	        Cost cost = new Cost();
	        cost.setMoney(102);
	        cost.setEntCode("2");
	        costService.insert(cost);
	        DataSourceContext.toDefault();
        }
        {
        	//----test 路由到主键指定的数据源.此测试指定数据源multi_1
        	DataSourceContext.setDataSourceRouterCode("multi_2");
        	Cost cost = new Cost();
        	cost.setMoney(200);
        	cost.setEntCode("2");
        	costService.insert2(cost);
        	DataSourceContext.toDefault();
        }
//        {
//        	//默认数据库,不建表会报错
//	        Cost cost = new Cost();
//	        cost.setMoney(102);
//	        cost.setEntCode("2");
//	        costService.insert(cost);
//        }
		return "hello,world";
	}
}
