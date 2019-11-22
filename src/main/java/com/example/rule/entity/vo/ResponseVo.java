package com.example.rule.entity.vo;

import com.alibaba.fastjson.JSON;
import com.example.rule.entity.enums.ResponseEnum;
import com.example.rule.entity.exception.ResponseException;

import java.io.Serializable;
import java.lang.reflect.Type;


public class ResponseVo<T> implements Serializable {

    private int status;
    private T data;

    private ResponseVo() {
    }

    private ResponseVo(int status) {
        this.status = status;
    }

    private ResponseVo(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ResponseVo(ResponseEnum responseEnum) {
        this.status = responseEnum.getCode();
        this.data = (T) responseEnum.getDesc();
    }

    public boolean hasSuccess() {
        return this.status == ResponseEnum.SUCCESS.getCode();
    }

    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public static <T> ResponseVo<T> success() {
        return new ResponseVo<T>(ResponseEnum.SUCCESS.getCode());
    }

    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<T>(ResponseEnum.SUCCESS.getCode(), data);
    }


    public static <String> ResponseVo<String> error(String errorMessage) {
        return new ResponseVo<String>(ResponseEnum.ERROR.getCode(), errorMessage);
    }

    public static <String> ResponseVo<String> error(int status, String errorMessage) {
        return new ResponseVo<String>(status, errorMessage);
    }

    public static <String> ResponseVo<String> error(ResponseEnum responseEnum) {
        return new ResponseVo<String>(responseEnum);
    }

    /**
     * 检查并返回参数
     * 当检查到返回状态为异常状态时，直接抛出异常
     *
     * @return
     */
    public T checkAndGetData() {
        if (status != ResponseEnum.SUCCESS.getCode()) {
            throw new ResponseException(this);
        }
        return JSON.parseObject(JSON.toJSONString(data), (Type) data.getClass());
    }

    /**
     * 检查并返回参数
     * 当检查到返回状态为异常状态时，直接抛出异常
     *
     * @param clazz 想要返回结果的类型
     * @param <E>
     * @return
     */
    public <E> E checkAndGetData(Class clazz) {
        if (status != ResponseEnum.SUCCESS.getCode()) {
            throw new ResponseException(this);
        }
        return JSON.parseObject(JSON.toJSONString(data), (Type) clazz);
    }


}
