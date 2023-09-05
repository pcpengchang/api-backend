package com.pc.project.apidomain.datagenerate.generator;

import com.pc.project.apistarter.model.request.datagenerator.TableSchemaRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 固定值数据生成器
 *
 * @author pengchang
 */
public class FixedDataGenerator implements DataGenerator {

    @Override
    public List<String> doGenerate(TableSchemaRequest.Field field, int rowNum) {
        String mockParams = field.getMockParams();
        if (StringUtils.isBlank(mockParams)) {
            mockParams = "6";
        }
        List<String> list = new ArrayList<>(rowNum);
        for (int i = 0; i < rowNum; i++) {
            list.add(mockParams);
        }
        return list;
    }
}
