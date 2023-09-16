package com.pc.project.apidomain.datagenerator.generator;


import com.pc.project.apistarter.enums.MockTypeEnum;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author pengchang
 * @date 2023/08/06 16:15
 **/
public class DataGeneratorFactory {
    /**
     * 模拟类型 => 生成器映射
     */
    private static final Map<MockTypeEnum, DataGenerator> MOCK_TYPE_DATA_GENERATOR_MAP = new HashMap<MockTypeEnum, DataGenerator>() {{
        put(MockTypeEnum.NONE, new DefaultDataGenerator());
        put(MockTypeEnum.FIXED, new FixedDataGenerator());
        put(MockTypeEnum.RANDOM, new RandomDataGenerator());
        put(MockTypeEnum.INCREASE, new IncreaseDataGenerator());
    }};

    private DataGeneratorFactory() {
    }

    /**
     * 获取实例
     *
     * @param mockTypeEnum
     * @return
     */
    public static DataGenerator getGenerator(MockTypeEnum mockTypeEnum) {
        mockTypeEnum = Optional.ofNullable(mockTypeEnum).orElse(MockTypeEnum.NONE);
        return MOCK_TYPE_DATA_GENERATOR_MAP.get(mockTypeEnum);
    }
}
