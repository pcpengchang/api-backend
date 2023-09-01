package com.pc.project.service;


import com.pc.apiclientsdk.client.ApiClient;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.annotation.Resource;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

@SpringBootTest
public class UserInterfaceInfoServiceTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private ApiClient apiClient;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Data
    class interfaceI {
        private String sdk;
    }

    @Test
    public void invokeCount() {
//        InterfaceInfo oldInterfaceInfo = interfaceInfoService.getById(2);
//        System.out.println(oldInterfaceInfo.getSdk());
//
//        interfaceI interfaceI = new interfaceI();
//        BeanUtils.copyProperties(oldInterfaceInfo, interfaceI);
//
//        System.out.println(interfaceI.getSdk());
    }

    @Test
    public void invokeInterface() throws Exception {
        Class<?> clientClazz = Class.forName("com.pc.apiclientsdk.client.AIClient");
        Constructor<?> binApiClientConstructor = clientClazz.getConstructor(String.class, String.class);
        Object apiClient = binApiClientConstructor.newInstance("121", "121");

        Method[] methods = clientClazz.getMethods();
        String json = "{\"text\":\"你好\"}";
        for (Method method : methods) {
            System.out.println(method.getName());
            if (method.getName().equals("doChat")) {
                // System.out.println(method.getParameterCount());
                System.out.println(doInvoke(apiClient, method, json));
            }
        }
    }

    private Object doInvoke(Object service, Method method, String param) throws Exception {
        System.out.println(method.getParameterTypes()[0]);
        // 无参方法
        if (null == param && method.getParameterCount() == 0) {
            return method.invoke(service);
        }
        // 序列化
        // Object parameter = new Gson().fromJson(param, method.getParameterTypes()[0]);
        // 单参方法
        return method.invoke(service, param);
    }

    @Resource
    private JavaMailSender javaMailSender;

    @Test
    public void process() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("1767191006@qq.com");
        message.setTo("1767191006@qq.com");
        message.setSubject("subject");
        message.setText("test");
        javaMailSender.send(message);
    }
}