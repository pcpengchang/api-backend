package com.pc.project.model.dto.search;

import com.pc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 * @author pengchang
 *  
 */
@Data
public class SearchRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

//    /**
//     * 类型
//     */
//    private String type;

    private static final long serialVersionUID = 1L;
}