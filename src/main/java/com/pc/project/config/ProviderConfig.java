//package com.pc.project.config;
//
//import com.pc.apicommon.service.InnerInterfaceInfoService;
//import com.pc.apicommon.service.InnerUserInterfaceInfoService;
//import com.pc.apicommon.service.InnerUserService;
//import com.pc.project.service.impl.inner.InnerInterfaceInfoServiceImpl;
//import com.pc.project.service.impl.inner.InnerUserInterfaceInfoServiceImpl;
//import com.pc.project.service.impl.inner.InnerUserServiceImpl;
//import org.apache.dubbo.config.ServiceConfig;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author pengchang
// * @date 2023/08/31 01:01
// **/
//@Configuration
//public class ProviderConfig {
//    @Bean(name = "innerInterfaceInfoService")
//    public ServiceConfig innerInterfaceInfoService() {
//        ServiceConfig<InnerInterfaceInfoService> service = new ServiceConfig<>();
//        service.setInterface(InnerInterfaceInfoService.class);
//        service.setRef(new InnerInterfaceInfoServiceImpl());
////        service.setTimeout(500);
////        service.setAsync(true);
//        return service;
//    }
//
//    @Bean(name = "innerUserInterfaceInfoService")
//    public ServiceConfig innerUserInterfaceInfoService() {
//        ServiceConfig<InnerUserInterfaceInfoService> service = new ServiceConfig<>();
//        service.setInterface(InnerUserInterfaceInfoService.class);
//        service.setRef(new InnerUserInterfaceInfoServiceImpl());
////        service.setTimeout(500);
////        service.setAsync(true);
//        return service;
//    }
//
//    @Bean(name = "innerUserService")
//    public ServiceConfig innerUserService() {
//        ServiceConfig<InnerUserService> service = new ServiceConfig<>();
//        service.setInterface(InnerUserService.class);
//        service.setRef(new InnerUserServiceImpl());
////        service.setTimeout(500);
////        service.setAsync(true);
//        return service;
//    }
//}