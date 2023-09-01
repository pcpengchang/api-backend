package com.pc.apiclientsdk.model;

import lombok.Data;

/**
 * @author pengchang
 * @date 2023/08/02 22:05
 **/
@Data
public class SearchRequest {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 条数
     */
    private Integer size;
}
