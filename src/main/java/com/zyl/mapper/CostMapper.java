package com.zyl.mapper;

import com.zyl.annotation.DynamicRoutingDataSource;
import com.zyl.annotation.ReadonlyDataSource;
import com.zyl.bean.Cost;

public interface CostMapper {

    int deleteByPrimaryKey(Integer id);

    
    int insert(Cost record);
    /**
     * 测试只路由到 multi_1 数据源
     * @param record
     * @return
     */
    @DynamicRoutingDataSource("multi_1_master")
    int insertSelective(Cost record);

    @ReadonlyDataSource
    Cost selectByPrimaryKey(Integer id);


    int updateByPrimaryKeySelective(Cost record);

    int updateByPrimaryKey(Cost record);
    
    int sum();
}