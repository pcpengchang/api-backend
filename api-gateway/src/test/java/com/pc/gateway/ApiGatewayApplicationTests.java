package com.pc.gateway;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.bootstrap.DubboBootstrap;
import org.apache.dubbo.config.utils.ReferenceConfigCache;
import org.apache.dubbo.rpc.service.GenericService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiGatewayApplicationTests {

    @Test
    void testGenericService() {
        ApplicationConfig application = new ApplicationConfig();
        application.setName("gateWay");
        application.setQosEnable(false);
        RegistryConfig registry = new RegistryConfig();
        registry.setAddress("nacos://120.25.220.64:8848");
        registry.setRegister(false);
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setInterface("com.pc.apicommon.service.IUserService");
//        reference.setVersion(version);
        reference.setGeneric("true");

        // 连接远程服务
        DubboBootstrap bootstrap = DubboBootstrap.getInstance();
        bootstrap.application(application).registry(registry).reference(reference).start();
        // 获取泛化接口
        ReferenceConfigCache cache = ReferenceConfigCache.getCache();
        GenericService genericService = cache.get(reference);

        System.out.println(genericService);
    }

}
