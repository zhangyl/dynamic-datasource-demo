package com.zyl.service;

import com.zyl.bean.Cost;

public interface CostService {
	void insert(Cost cost);
	void insert2(Cost cost);
	
	Cost selectByPrimaryKey(Integer id);
	
}
