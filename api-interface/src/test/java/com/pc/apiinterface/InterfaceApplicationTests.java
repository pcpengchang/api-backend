package com.pc.apiinterface;

import com.pc.apiinterface.manager.AiManager;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class InterfaceApplicationTests {

    @Resource
    private AiManager aiManager;

    @Test
    void contextLoads() {
        System.out.println(aiManager.doChat(1685519303849390082L, "你知道人类吗"));
    }

}
