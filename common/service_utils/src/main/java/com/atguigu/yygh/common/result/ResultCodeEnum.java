package com.atguigu.yygh.common.result;

public enum ResultCodeEnum {
    SUCCESS(true, 20000, "成功"),
    ERROR(false, 20001, "失败");
    private Boolean flag;
    private Integer code;
    private String message;

    private ResultCodeEnum(Boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Boolean getFlag() {
        return flag;
    }
    public Integer getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }

    public void setFlag(Boolean flag) {
        this.flag = flag;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
