package com.zyl.datasource;

/**
 * 数据源操作类
 */
public class DataSourceContext {
	
	private static ThreadLocal<String> threadLocalDataSource = new InheritableThreadLocal<>();

	/**
	 * 获取数据源
	 */
	public static String getDataSource() {
		return threadLocalDataSource.get();
	}
	/**
	 * 设置数据源
	 */
	public static void setDataSource(String dataSource) {
		threadLocalDataSource.set(dataSource);
	}
	/**
	 * 清除数据源
	 * 清除后,数据源为默认时间
	 */
	public static void toDefault() {
		threadLocalDataSource.remove();
	}
}
