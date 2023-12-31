package com.atguigu.yygh.enums;

public enum StatusEnum {
    LOCK(0, "锁定"),
    NORMAL(1, "正常");
    Integer status;
    String statusString;
    public static String getStatusNameByStatus(Integer status) {
        StatusEnum[] values = StatusEnum.values();
        for (StatusEnum value : values) {
            if (value.getStatus() == status) {
                return value.getStatusString();
            }
        }
        return "";
    }
    StatusEnum(Integer status, String statusString) {
        this.status = status;
        this.statusString = statusString;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getStatusString() {
        return statusString;
    }

    public void setStatusString(String statusString) {
        this.statusString = statusString;
    }
}
