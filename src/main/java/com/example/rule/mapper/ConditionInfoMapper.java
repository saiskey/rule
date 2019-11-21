package com.example.rule.mapper;

import com.example.rule.entity.pojo.ConditionInfo;

public interface ConditionInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(ConditionInfo record);

    int insertSelective(ConditionInfo record);

    ConditionInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ConditionInfo record);

    int updateByPrimaryKey(ConditionInfo record);

    int findByName(String conditionName);
}