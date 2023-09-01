package com.pc.project.model.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pengchang
 * @date 2023/08/14 22:45
 **/
@Data
public class ReportAddRequest implements Serializable {

    /**
     * 内容
     */
    private String content;

    /**
     * 被举报对象 id
     */
    private Long reportedId;

    private static final long serialVersionUID = 1L;
}