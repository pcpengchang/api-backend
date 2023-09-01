//package com.pc.gateway;
//
//import com.pc.apicommon.service.InnerInterfaceInfoService;
//import com.pc.apicommon.service.InnerUserInterfaceInfoService;
//import com.pc.apicommon.service.InnerUserService;
//import org.apache.dubbo.config.ReferenceConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author pengchang
// * @date 2023/08/31 01:01
// **/
//@Configuration
//public class ConsumerConfig {
//    @Bean(name = "dubboInterfaceInfoService")
//    public ReferenceConfig innerInterfaceInfoService() {
//        ReferenceConfig<InnerInterfaceInfoService> service = new ReferenceConfig<>();
//        service.setInterface(InnerInterfaceInfoService.class);
////        service.setTimeout(500);
////        service.setAsync(true);
////        service.setClient("netty");
//        service.setRetries(1);
//        return service;
//    }
//
//    @Bean(name = "dubboUserInterfaceInfoService")
//    public ReferenceConfig innerUserInterfaceInfoService() {
//        ReferenceConfig<InnerUserInterfaceInfoService> service = new ReferenceConfig<>();
//        service.setInterface(InnerUserInterfaceInfoService.class);
////        service.setTimeout(500);
////        service.setAsync(true);
////        service.setClient("netty");
//        service.setRetries(1);
//        return service;
//    }
//
//    @Bean(name = "dubboUserService")
//    public ReferenceConfig innerUserService() {
//        ReferenceConfig<InnerUserService> service = new ReferenceConfig<>();
//        service.setInterface(InnerUserService.class);
////        service.setTimeout(500);
////        service.setAsync(true);
////        service.setClient("netty");
//        service.setRetries(1);
//        return service;
//    }
//}