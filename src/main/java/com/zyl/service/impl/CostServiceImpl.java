package com.zyl.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.zyl.bean.Cost;
import com.zyl.mapper.CostMapper;
import com.zyl.service.CostService;

@Service
public class CostServiceImpl implements CostService {

	@Resource
	private CostMapper costMapper;

	@Override
	public void insert(Cost cost)  {
		System.out.println("insert:" + costMapper.insert(cost));
	}

	@Override
	public void insert2(Cost cost)  {
		System.out.println("insert2:" + costMapper.insertSelective(cost));
	}
	
	public Cost selectByPrimaryKey(Integer id) {
		return costMapper.selectByPrimaryKey(id);
	}

}