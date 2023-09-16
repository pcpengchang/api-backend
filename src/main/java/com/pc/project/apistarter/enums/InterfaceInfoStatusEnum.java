package com.pc.project.apistarter.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 *
 * @author pengchang
 */
public enum InterfaceInfoStatusEnum {

    OFFLINE("关闭", 0),
    ONLINE("上线", 1);

    private final String text;

    private final int value;

    InterfaceInfoStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    public static InterfaceInfoStatusEnum findByCode(Integer value) {
        return Arrays.stream(InterfaceInfoStatusEnum.values()).filter(typeEnum -> Objects.equals(typeEnum.getValue(), value))
                .findFirst().orElse(null);
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
