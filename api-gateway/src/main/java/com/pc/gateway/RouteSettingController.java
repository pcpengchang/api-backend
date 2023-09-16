package com.pc.gateway;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/09/06 11:04
 **/
@RestController
@RequestMapping("/route")
public class RouteSettingController {

    @Resource
    private DynamicRouteServiceImpl dynamicRouteService;

    @PostMapping("/add")
    public String add(@RequestBody MyRouteDefinition myRouteDefinition) {
        try {
            dynamicRouteService.add(myRouteDefinition);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }

    @PostMapping("delete")
    public String delete(String id) {
        try {
            dynamicRouteService.delete(id);
            return "success";
        } catch (Exception e) {
            return "error";
        }
    }
}