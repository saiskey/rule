package com.example.rule.entity.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/5 10:11
 **/
@Data
public class RuleDto {
    //规则id
    private Long ruleId;
    //规则名
    private String ruleName;
    //执行模板名
    private String templateName;
    //执行模板方法名
    private String templateMethodName;
    //模板参数
    private List<String> fieldType;
    //规则参数类型
    private List<String> paramsType;
    //规则执行参数
    private Map<String, String> params;
    //条件id
    private List<Long> conditionIds;
    //命中规则（数量）
    private int hitCount;
    //命中规则（自定义组合）
    private List<List<String>> hitRule;
    //条件id
    private Long conditionId;
    //条件名称
    private String conditionName;
    //前置内容
    private String preContent;
    //判断条件
    private String judgeContent;
    //命中后操作内容
    private String hitContent;
    //完整规则内容
    private String ruleContent;
    //操作后返回类型
    private String returnType;
}
