
package com.zyl.mapper;

import java.util.List;

import com.zyl.annotation.DynamicRoutingDataSource;
import com.zyl.bean.DataSourceDO;

/**
 * Created by yizhenn on 2016/12/4.
 */
public interface DataSourceMapper {
	@DynamicRoutingDataSource("defaultDataSource")
    List<DataSourceDO> getAllDataSources();

}
