package com.atguigu.yygh.common.Exception;

public class yyghException extends RuntimeException{
    Integer code;
    String msg;

    public yyghException( Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
