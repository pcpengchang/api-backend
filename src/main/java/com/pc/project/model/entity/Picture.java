package com.pc.project.model.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author pengchang
 * @date 2023/03/22 19:14
 **/
@Data
public class Picture implements Serializable {

    private String title;

    private String url;

    private static final long serialVersionUID = 1L;

}