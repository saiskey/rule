package com.example.rule.entity.dto;

import lombok.Data;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/14 16:53
 **/
@Data
public class ConditionDto extends RuleDto {
    //条件id
    private Long conditionId;
    //条件名称
    private String conditionName;
    //条件内容
    private String conditionContent;

}
