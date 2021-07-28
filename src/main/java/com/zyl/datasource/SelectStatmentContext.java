package com.zyl.datasource;

public class SelectStatmentContext {
	/**
	 * 用于判断是否是查询，做读写分离
	 * 
	 */
	private static ThreadLocal<Boolean> threadLocal = new InheritableThreadLocal<>() {
		public Boolean initialValue() {
			return false;
		}
	};
	
	public static boolean isSelectStatment() {
		return threadLocal.get();
	}
	public static void setSelectStatment(boolean isSelectStatment) {
		threadLocal.set(isSelectStatment);
	}
	
	public static void clear() {
		threadLocal.remove();
	}
	
}
