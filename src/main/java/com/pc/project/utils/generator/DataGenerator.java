package com.pc.project.utils.generator;

import com.pc.project.model.entity.TableSchema;

import java.util.List;

/**
 * @author pengchang
 * @date 2023/08/06 16:16
 **/
public interface DataGenerator {
    /**
     * 生成
     *
     * @param field 字段信息
     * @param rowNum 行数
     * @return 生成的数据列表
     */
    List<String> doGenerate(TableSchema.Field field, int rowNum);
}

