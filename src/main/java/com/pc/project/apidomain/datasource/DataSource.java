package com.pc.project.apidomain.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 数据源接口（新接入的数据源必须实现）
 *
 * @author pengchang
 * @date 2023/03/31 15:25
 **/
public interface DataSource<T> {
    /**
     * 搜索
     *
     * @param searchText
     * @param pageNum
     * @param pageSize
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
