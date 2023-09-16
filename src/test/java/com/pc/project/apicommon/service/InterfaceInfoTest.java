package com.pc.project.apicommon.service;

import cn.hutool.http.HttpRequest;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.project.apiinfrastructure.mapper.InterfaceInfoMapper;
import com.pc.project.apistarter.enums.InterfaceInfoStatusEnum;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author pengchang
 * @date 2023/09/09 14:23
 **/
//@SpringBootTest
public class InterfaceInfoTest {
    @Resource
    private InterfaceInfoMapper mapper;

    @Test
    void testGet() {
        InterfaceInfo oldInfo = mapper.selectById(10);
        InterfaceInfo newInfo = mapper.selectById(10);
        newInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());

        System.out.println(oldInfo);
        System.out.println(newInfo);
        System.out.println(oldInfo.hashCode());
        System.out.println(newInfo.hashCode());
        System.out.println(Objects.equals(oldInfo, newInfo));
        System.out.println(Objects.equals(oldInfo, newInfo));
    }

    @Test
    void testTranslate() {
        String url = String.format("http://api.btstu.cn/tst/api.php?text=%s", "你好");
        String result = HttpRequest
                .get(url)
                .header("text", "你好")
                .execute()
                .body();
        System.out.println(result);
    }
}
