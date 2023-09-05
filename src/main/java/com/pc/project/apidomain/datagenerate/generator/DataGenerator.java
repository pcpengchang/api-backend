package com.pc.project.apidomain.datagenerate.generator;

import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;

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
    List<String> doGenerate(TableSchemaRequest.Field field, int rowNum);
}

