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
    private Map<String,String> params;
    private String ruleContent;

}
