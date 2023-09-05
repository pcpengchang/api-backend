package com.pc.project.apidomain.datagenerate.generator;

import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import com.pc.project.apistarter.enums.MockParamsRandomTypeEnum;
import com.pc.project.apistarter.utils.FakerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 随机值数据生成器
 *
 * @author pengchang
 */
public class RandomDataGenerator implements DataGenerator {

    @Override
    public List<String> doGenerate(TableSchemaRequest.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            MockParamsRandomTypeEnum randomTypeEnum = Optional.ofNullable(
                            MockParamsRandomTypeEnum.getEnumByValue(mockParams))
                    .orElse(MockParamsRandomTypeEnum.STRING);
            String randomString = FakerUtils.getRandomValue(randomTypeEnum);
            list.add(randomString);
        }
        return list;
    }
}
