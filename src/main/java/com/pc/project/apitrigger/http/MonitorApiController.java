package com.pc.project.apitrigger.http;

import com.alibaba.nacos.api.config.annotation.NacosValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengchang
 * @date 2023/08/06 21:32
 **/
@RestController
@RequestMapping("/monitor")
public class MonitorApiController {
    @NacosValue(value = "${config.value}", autoRefreshed = true)
    private String name;

    @RequestMapping("/alive")
    public String alive() {
        return name;
    }
}
