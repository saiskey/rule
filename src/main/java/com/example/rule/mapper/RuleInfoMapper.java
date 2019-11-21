package com.example.rule.mapper;

import com.example.rule.entity.pojo.RuleInfo;

public interface RuleInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RuleInfo record);

    int insertSelective(RuleInfo record);

    RuleInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RuleInfo record);

    int updateByPrimaryKey(RuleInfo record);

    int findCountByName(String name);

    RuleInfo findOneByName(String name);
}