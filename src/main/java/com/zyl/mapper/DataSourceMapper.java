
package com.zyl.mapper;

import java.util.List;

import com.zyl.annotation.DynamicRoutingDataSource;
import com.zyl.bean.DataSourceDO;

@DynamicRoutingDataSource("defaultDataSource")
public interface DataSourceMapper {
	
    List<DataSourceDO> getAllDataSources();

}
