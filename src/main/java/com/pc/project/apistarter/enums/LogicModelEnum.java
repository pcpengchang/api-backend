package com.pc.project.apistarter.enums;

/**
 * @author pengchang
 * @date 2023/09/16 21:27
 **/
public enum LogicModelEnum {
    ACCESS_LIMIT("ACCESS_LIMIT", "访问次数过滤");

    private String code;
    private String info;

    LogicModelEnum(String code, String info) {
        this.code = code;
        this.info = info;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}
