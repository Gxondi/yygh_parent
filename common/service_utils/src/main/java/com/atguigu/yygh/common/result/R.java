package com.atguigu.yygh.common.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
//统一返回结果使用  用于返回json数据
@Data
public class R {
    private boolean success;
    private Integer code;
    private String message;
    private Map<String,Object> data = new HashMap<String,Object>();
    private R() {};
    public static R ok() {
        R r = new R();
        r.success = ResultCodeEnum.SUCCESS.getFlag();
        r.code = ResultCodeEnum.SUCCESS.getCode();
        r.message = ResultCodeEnum.SUCCESS.getMessage();
        return r;
    }
    public static R error() {
        R r = new R();
        r.success = ResultCodeEnum.ERROR.getFlag();
        r.code = ResultCodeEnum.ERROR.getCode();
        r.message = ResultCodeEnum.ERROR.getMessage();
        return r;
    }
    public R success(Boolean success) {
        this.success = success;
        return this;
    }
    public R message(String message) {
        this.message = message;
        return this;
    }
    public R code(Integer code) {
        this.code = code;
        return this;
    }
    public R data(String key,Object value) {
        this.data.put(key, value);
        return this;
    }
    public R data(Map<String,Object> map) {
        this.setData(map);
        return this;
    }
}
