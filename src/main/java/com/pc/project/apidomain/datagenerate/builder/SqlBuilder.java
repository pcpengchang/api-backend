package com.pc.project.apidomain.datagenerate.builder;

import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest.Field;
import com.pc.project.apistarter.enums.FieldTypeEnum;
import com.pc.project.apistarter.enums.MockTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author pengchang
 * @date 2023/08/06 16:48
 **/
@Slf4j
public class SqlBuilder {
    /**
     * 封装字段名
     *
     * @param name
     * @return
     */
    public String wrapFieldName(String name) {
        return String.format("`%s`", name);
    }

    /**
     * 解析字段名
     *
     * @param fieldName
     * @return
     */
    public String parseFieldName(String fieldName) {
        if (fieldName.startsWith("`") && fieldName.endsWith("`")) {
            return fieldName.substring(1, fieldName.length() - 1);
        }
        return fieldName;
    }

    /**
     * 包装表名
     *
     * @param name
     * @return
     */
    public String wrapTableName(String name) {
        return String.format("`%s`", name);
    }

    /**
     * 解析表名
     *
     * @param tableName
     * @return
     */
    public String parseTableName(String tableName) {
        if (tableName.startsWith("`") && tableName.endsWith("`")) {
            return tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构造建表 SQL 核心函数
     *
     * @param tableSchema 表概要
     * @return 生成的 SQL
     */
    public String buildCreateTableSql(TableSchemaRequest tableSchema) {
        // 构造模板
        String template = "%s\n"
                + "create table if not exists %s\n"
                + "(\n"
                + "%s\n"
                + ") %s;";

        // 构造数据库名 + 表名
        String tableName = wrapTableName(tableSchema.getTableName());
        String dbName = tableSchema.getDbName();
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }

        // 构造表前缀注释
        String tableComment = tableSchema.getTableComment();
        if (StringUtils.isBlank(tableComment)) {
            // 表名代替注释
            tableComment = tableName;
        }
        String tablePrefixComment = String.format("-- %s", tableComment);

        // 构造表后缀注释
        String tableSuffixComment = String.format("comment '%s'", tableComment);

        // 构造表所有字段 建表核心
        List<Field> fieldList = tableSchema.getFieldList();

        // 使用 StringBuilder 适合大量增加
        StringBuilder fieldStrBuilder = new StringBuilder();
        int fieldSize = fieldList.size();
        for (int i = 0; i < fieldSize; i++) {
            Field field = fieldList.get(i);
            fieldStrBuilder.append(buildCreateFieldSql(field));
            // 最后一个字段后没有逗号和换行
            if (i != fieldSize - 1) {
                fieldStrBuilder.append(",");
                fieldStrBuilder.append("\n");
            }
        }
        // 转回字符串
        String fieldStr = fieldStrBuilder.toString();

        // 填充模板
        String result = String.format(template, tablePrefixComment, tableName, fieldStr, tableSuffixComment);
        log.info("sql result = " + result);
        return result;
    }

    /**
     * 生成创建字段的 SQL
     *
     * @param field
     * @return
     */
    public String buildCreateFieldSql(Field field) {
        if (field == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 按照日常建表规则写即可
        String fieldName = wrapFieldName(field.getFieldName());
        String fieldType = field.getFieldType();
        String defaultValue = field.getDefaultValue();
        boolean notNull = field.isNotNull();
        String comment = field.getComment();
        String onUpdate = field.getOnUpdate();
        boolean primaryKey = field.isPrimaryKey();
        boolean autoIncrement = field.isAutoIncrement();
        // e.g. column_name int default 0 not null auto_increment comment '注释' primary key,
        StringBuilder fieldStrBuilder = new StringBuilder();
        // 字段名
        fieldStrBuilder.append(fieldName);
        // 字段类型
        fieldStrBuilder.append(" ").append(fieldType);
        // TODO: 加入数据范围
        // 默认值
        if (StringUtils.isNotBlank(defaultValue)) {
            fieldStrBuilder.append(" ").append("default ").append(getValueStr(field, defaultValue));
        }
        // 是否非空
        fieldStrBuilder.append(" ").append(notNull ? "not null" : "null");
        // 是否自增
        if (autoIncrement) {
            fieldStrBuilder.append(" ").append("auto_increment");
        }
        // 附加条件 需要更新的字段
        if (StringUtils.isNotBlank(onUpdate)) {
            fieldStrBuilder.append(" ").append("on update ").append(onUpdate);
        }
        // 注释
        if (StringUtils.isNotBlank(comment)) {
            fieldStrBuilder.append(" ").append(String.format("comment '%s'", comment));
        }
        // 是否为主键
        if (primaryKey) {
            fieldStrBuilder.append(" ").append("primary key");
        }
        return fieldStrBuilder.toString();
    }

    /**
     * 构造插入数据 SQL 可以插入多条
     * e.g. INSERT INTO report (id, content) VALUES (1, '这个有点问题吧');
     *
     * @param tableSchema 表概要
     * @param dataList    数据列表
     * @return 生成的 SQL 列表字符串
     */
    public String buildInsertSql(TableSchemaRequest tableSchema, List<Map<String, Object>> dataList) {
        // 构造模板
        String template = "insert into %s (%s) values (%s);";
        // 同上构造表名
        String dbName = tableSchema.getDbName();
        String tableName = wrapTableName(tableSchema.getTableName());
        if (StringUtils.isNotBlank(dbName)) {
            tableName = String.format("%s.%s", dbName, tableName);
        }
        // 构造表字段
        List<Field> fieldList = tableSchema.getFieldList();
        // 过滤掉不模拟的字段  这部分字段不参与到模拟数据的生成
        // 保证是 非 none 的
        fieldList = fieldList.stream()
                .filter(field -> {
                    MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                            .orElse(MockTypeEnum.NONE);
                    return !MockTypeEnum.NONE.equals(mockTypeEnum);
                }).collect(Collectors.toList());

        // 字段名 'xxx' 和对应的值
        String keyStr = fieldList.stream()
                .map(field -> wrapFieldName(field.getFieldName()))
                .collect(Collectors.joining(", "));

        StringBuilder resultStringBuilder = new StringBuilder();
        int total = dataList.size();
        for (int i = 0; i < total; i++) {
            Map<String, Object> dataRow = dataList.get(i);

            String valueStr = fieldList.stream()
                    .map(field -> getValueStr(field, dataRow.get(field.getFieldName())))
                    .collect(Collectors.joining(", "));
            // 填充模板
            String result = String.format(template, tableName, keyStr, valueStr);
            resultStringBuilder.append(result);
            // 最后一个字段后没有换行
            if (i != total - 1) {
                resultStringBuilder.append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

    /**
     * 根据列的属性获取值字符串
     *
     * @param field
     * @param value
     * @return
     */
    public static String getValueStr(Field field, Object value) {
        if (field == null || value == null) {
            return "''";
        }
        FieldTypeEnum fieldTypeEnum = Optional.ofNullable(FieldTypeEnum.getEnumByValue(field.getFieldType()))
                .orElse(FieldTypeEnum.TEXT);
        switch (fieldTypeEnum) {
            case DATE:
            case TIME:
            case DATETIME:
            case CHAR:
            case VARCHAR:
            case TINYTEXT:
            case TEXT:
            case MEDIUMTEXT:
            case LONGTEXT:
            case TINYBLOB:
            case BLOB:
            case MEDIUMBLOB:
            case LONGBLOB:
            case BINARY:
            case VARBINARY:
                return String.format("'%s'", value);
            default:
                return String.valueOf(value);
        }
    }
}
