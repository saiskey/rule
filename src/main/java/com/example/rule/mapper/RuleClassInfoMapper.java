package com.example.rule.mapper;

import com.example.rule.entity.pojo.RuleClassInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RuleClassInfoMapper {
    int deleteByPrimaryKey(Long id);

    int insert(RuleClassInfo record);

    int insertSelective(RuleClassInfo record);

    RuleClassInfo selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RuleClassInfo record);

    int updateByPrimaryKey(RuleClassInfo record);

//    RuleClassInfo findByTemplateMethod(@Param("templateName") String templateName,
//                                       @Param("templateMethodName") String templateMethodName);
}