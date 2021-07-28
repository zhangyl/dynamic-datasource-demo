package com.zyl.datasource;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 拦截mybatis sql
 *
 * @author zyl
 *
 */

@Intercepts({ 
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
	@Signature(type = Executor.class, method = "queryCursor", args = { MappedStatement.class, Object.class, RowBounds.class}),
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }),
	@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class })
})
public class ZylSqlInterceptor implements Interceptor {
	final Logger log = LoggerFactory.getLogger(ZylSqlInterceptor.class);
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
			//如果是
			if(SqlCommandType.SELECT == mappedStatement.getSqlCommandType()) {
				SelectStatmentContext.setSelectStatment(true);
			}
//			Method method = invocation.getMethod();
//			DynamicRoutingDataSource annotation = method.getAnnotation(DynamicRoutingDataSource.class);
//			String v = annotation.value();
//			log.info("----------------v=" + v);
			Object result = invocation.proceed();
			
			return result;
			
		} finally {
			SelectStatmentContext.clear();
		}
		
	}

}
