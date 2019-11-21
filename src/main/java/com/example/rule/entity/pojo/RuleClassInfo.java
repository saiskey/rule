package com.example.rule.entity.pojo;

import java.util.Date;

public class RuleClassInfo {
    private Long id;

    private String className;

    private String methodName;

    private String codeBody;

    private String methodParamType;

    private String fieldType;

    private Date gmtCreate;

    private Date gmtUpdate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className == null ? null : className.trim();
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName == null ? null : methodName.trim();
    }

    public String getCodeBody() {
        return codeBody;
    }

    public void setCodeBody(String codeBody) {
        this.codeBody = codeBody == null ? null : codeBody.trim();
    }

    public String getMethodParamType() {
        return methodParamType;
    }

    public void setMethodParamType(String methodParamType) {
        this.methodParamType = methodParamType == null ? null : methodParamType.trim();
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType == null ? null : fieldType.trim();
    }

    public Date getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Date gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Date getGmtUpdate() {
        return gmtUpdate;
    }

    public void setGmtUpdate(Date gmtUpdate) {
        this.gmtUpdate = gmtUpdate;
    }
}