package com.example.rule.entity.exception;

import com.example.rule.entity.enums.ResponseEnum;
import com.example.rule.entity.vo.ResponseVo;
import lombok.Data;

/**
 * @Auther: Tt
 * @Date: 2019/3/30 17:49
 * @Description:
 */
@Data
public class ResponseException extends RuntimeException {

    private int code;

    private String msg;

    public ResponseException(ResponseEnum responseEnum) {
        super(responseEnum.getDesc());
        this.msg = responseEnum.getDesc();
        this.code = responseEnum.getCode();
    }

    public ResponseException(ResponseEnum responseEnum, String errMsg) {
        super(errMsg);
        this.code = responseEnum.getCode();
        this.msg = errMsg;
    }

    public ResponseException(ResponseVo responseVo) {
        super(responseVo.getData().toString());
        this.code = responseVo.getStatus();
        this.msg = responseVo.getData().toString();
    }

    public ResponseException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public ResponseException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public ResponseException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public ResponseException() {
        super();
    }

}
