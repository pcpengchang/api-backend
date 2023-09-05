package com.pc.project;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author 18128332989
 */
@SpringBootApplication
@MapperScan("com.pc.project")
@EnableDubbo
//@NacosPropertySource(dataId = "common", type = ConfigType.YAML, autoRefreshed = true)
public class MyApplication {

    public static void main(String[] args) {
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        SpringApplication.run(MyApplication.class, args);
    }

}
