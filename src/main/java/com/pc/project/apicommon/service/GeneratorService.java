package com.pc.project.apicommon.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.gson.GsonBuilder;
import com.pc.project.apidomain.datagenerator.builder.DataBuilder;
import com.pc.project.apidomain.datagenerator.builder.JavaCodeBuilder;
import com.pc.project.apidomain.datagenerator.builder.SqlBuilder;
import com.pc.project.apistarter.exception.SchemaException;
import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest.Field;
import com.pc.project.apistarter.model.vo.GenerateVO;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/08/06 16:07
 **/
@Service
public class GeneratorService {
    public GenerateVO generateAll(TableSchemaRequest tableSchema) throws TemplateException, IOException {
        // 校验模拟条数和生成规则
        validSchema(tableSchema);
        int mockNum = tableSchema.getMockNum();
        // 生成模拟数据 一列一列生成
        List<Map<String, Object>> dataList = DataBuilder.generateData(tableSchema, mockNum);
        // 数据 转 json
        String dataJson = new GsonBuilder()
                .setPrettyPrinting()
                .create().toJson(dataList);
        // 生成 java 实体代码
        String javaEntityCode = JavaCodeBuilder.buildJavaEntityCode(tableSchema);
        // 生成 java 对象代码
        String javaObjectCode = JavaCodeBuilder.buildJavaObjectCode(tableSchema, dataList);

        SqlBuilder sqlBuilder = new SqlBuilder();
        String createSql = sqlBuilder.buildCreateTableSql(tableSchema);
        String insertSql = sqlBuilder.buildInsertSql(tableSchema, dataList);

        GenerateVO generateVO = new GenerateVO();
        generateVO.setTableSchema(tableSchema);
        generateVO.setCreateSql(createSql);
        generateVO.setDataList(dataList);
        generateVO.setInsertSql(insertSql);
        generateVO.setDataJson(dataJson);
        generateVO.setJavaEntityCode(javaEntityCode);
        generateVO.setJavaObjectCode(javaObjectCode);
        return generateVO;
    }

    /**
     * 验证 schema
     *
     * @param tableSchema 表概要
     */
    public static void validSchema(TableSchemaRequest tableSchema) {
        if (tableSchema == null) {
            throw new SchemaException("数据为空");
        }
        String tableName = tableSchema.getTableName();
        if (StringUtils.isBlank(tableName)) {
            throw new SchemaException("表名不能为空");
        }
        Integer mockNum = tableSchema.getMockNum();
        // 默认生成 20 条
        if (tableSchema.getMockNum() == null) {
            tableSchema.setMockNum(20);
            mockNum = 20;
        }
        if (mockNum > 100 || mockNum < 10) {
            throw new SchemaException("生成条数设置错误");
        }
        List<Field> fieldList = tableSchema.getFieldList();
        if (CollectionUtils.isEmpty(fieldList)) {
            throw new SchemaException("字段列表不能为空");
        }
        for (Field field : fieldList) {
            validField(field);
        }
    }

    /**
     * 校验字段
     *
     * @param field
     */
    public static void validField(Field field) {
        String fieldName = field.getFieldName();
        String fieldType = field.getFieldType();
        if (StringUtils.isBlank(fieldName)) {
            throw new SchemaException("字段名不能为空");
        }
        if (StringUtils.isBlank(fieldType)) {
            throw new SchemaException("字段类型不能为空");
        }
    }

}
