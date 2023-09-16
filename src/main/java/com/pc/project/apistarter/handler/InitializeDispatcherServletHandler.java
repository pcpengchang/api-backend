package com.pc.project.apistarter.handler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/09/06 00:24
 **/
@Component
public class InitializeDispatcherServletHandler implements CommandLineRunner {

    @Resource
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) {
        String url = "http://localhost:7529/api/monitor/alive";
        try {
            restTemplate.execute(url, HttpMethod.GET, null, null);
        } catch (Throwable ignored) {
        }
    }
}
