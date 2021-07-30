
package com.zyl.mapper;

import java.util.List;

import com.zyl.annotation.DynamicRoutingDataSource;
import com.zyl.bean.DataSourceDO;


public interface DataSourceMapper {
	@DynamicRoutingDataSource("defaultDataSource")
    List<DataSourceDO> getAllDataSources();

}
