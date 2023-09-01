package com.pc.project.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.pc.project.model.entity.TableSchema;
import com.pc.project.model.entity.TableSchema.Field;
import com.pc.project.model.enums.MockTypeEnum;
import com.pc.project.utils.generator.DataGenerator;

import java.util.*;

/**
 * @author pengchang
 * @date 2023/08/06 16:13
 **/
public class DataBuilder {
    /**
     * 一列一列生成数据
     * 一个字段名对应数据
     *
     * @param tableSchema
     * @param rowNum
     * @return
     */
    public static List<Map<String, Object>> generateData(TableSchema tableSchema, int rowNum) {
        List<Field> fieldList = tableSchema.getFieldList();
        // 初始化结果数据
        List<Map<String, Object>> resultList = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            resultList.add(new HashMap<>());
        }

        // 遍历所有字段  依次生成每一列  从上到下 从左到右
        for (Field field : fieldList) {

            // 空为不模拟  其他有递增 固定 随机 规则 词库
            MockTypeEnum mockTypeEnum = Optional.ofNullable(MockTypeEnum.getEnumByValue(field.getMockType()))
                    .orElse(MockTypeEnum.NONE);

            // 半单例 字符串值 ==> 枚举类 ==> 生成方法
            // 工厂模式 找到对应的类和方法  避免if else
            DataGenerator dataGenerator = DataGeneratorFactory.getGenerator(mockTypeEnum);

            // 子类去跑具体的方法, 方便写子类去扩充更多的生成器
            List<String> mockDataList = dataGenerator.doGenerate(field, rowNum);
            String fieldName = field.getFieldName();

            // 填充结果列表
            if (CollectionUtils.isNotEmpty(mockDataList)) {
                for (int i = 0; i < rowNum; i++) {
                    resultList.get(i).put(fieldName, mockDataList.get(i));
                }
            }
        }
        return resultList;
    }
}
