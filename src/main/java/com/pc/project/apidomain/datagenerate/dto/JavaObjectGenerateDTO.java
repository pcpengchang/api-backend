package com.pc.project.apidomain.datagenerate.dto;

import lombok.Data;

import java.util.List;

/**
 * @author pengchang
 * @date 2023/08/06 16:41
 **/
@Data
public class JavaObjectGenerateDTO {

    /**
     * 类名
     */
    private String className;

    /**
     * 对象名
     */
    private String objectName;

    /**
     * 列信息列表
     */
    private List<FieldDTO> fieldList;

    /**
     * 列信息
     */
    @Data
    public static class FieldDTO {
        /**
         * set 方法名
         */
        private String setMethod;

        /**
         * 值
         */
        private String value;
    }

}
