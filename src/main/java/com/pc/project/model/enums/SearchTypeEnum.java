package com.pc.project.model.enums;

import org.apache.commons.lang3.ObjectUtils;

/**
 * 搜索类型枚举
 *
 * @author pengchang
 *  
 */
public enum SearchTypeEnum {

    POST("帖子", "post"),
    USER("用户", "user"),
    PICTURE("图片", "picture"),
    VIDEO("视频", "video");

    private final String text;

    private final String value;

    SearchTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static SearchTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (SearchTypeEnum anEnum : SearchTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
