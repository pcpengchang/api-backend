package com.pc.project.apistarter.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author pengchang
 * @date 2023/09/07 15:16
 **/
@Component
public class NacosListenerConfig implements InitializingBean {
    @Value("${nacos.config.server-addr}")
    private String serverAddr;

    @Value("${nacos.config.data-id}")
    private String dataId;

    @Value("${nacos.config.group}")
    private String group;

    @Override
    public void afterPropertiesSet() throws Exception {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        ConfigService configService = NacosFactory.createConfigService(properties);
        configService.addListener(dataId, group, new Listener() {
            @Override
            public void receiveConfigInfo(String configInfo) {
                System.out.println(configInfo);
            }

            @Override
            public Executor getExecutor() {
                return null;
            }
        });
    }
}
