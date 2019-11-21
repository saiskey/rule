package com.example.rule.entity.enums;

/**
 * @Description:
 * @Author: wangzhuo
 * @Date: 2019/11/7 14:32
 **/
public enum ParamTypeEnum {
    STRING(1, "String"),
    INT(2, "int"),
    LONG(3, "Long"),
    ;

    private int code;
    private String desc;

    ParamTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
