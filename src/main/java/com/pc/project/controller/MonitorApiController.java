package com.pc.project.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author pengchang
 * @date 2023/08/06 21:32
 **/
@RestController
@RequestMapping("/monitor")
public class MonitorApiController {

    @RequestMapping("/alive")
    public String alive() {
        return "ok";
    }
}
