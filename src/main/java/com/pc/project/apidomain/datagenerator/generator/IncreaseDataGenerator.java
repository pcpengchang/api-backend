package com.pc.project.apidomain.datagenerator.generator;

import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 递增值数据生成器
 *
 * @author pengchang
 */
public class IncreaseDataGenerator implements DataGenerator {

    @Override
    public List<String> doGenerate(TableSchemaRequest.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        List<String> list = new ArrayList<>(rowNum);
        if (StringUtils.isBlank(mockParams)) {
            mockParams = "1";
        }
        int initValue = Integer.parseInt(mockParams);
        for (int i = 0; i < rowNum; i++) {
            list.add(String.valueOf(initValue + i));
        }
        return list;
    }
}
