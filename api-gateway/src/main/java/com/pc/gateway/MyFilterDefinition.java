package com.pc.gateway;

import java.util.HashMap;
import java.util.Map;

/**
 * @author pengchang
 * @date 2023/09/06 11:01
 **/
public class MyFilterDefinition {
    private String name;

    private Map<String,String> args = new HashMap<>();


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getArgs() {
        return args;
    }

    public void setArgs(Map<String, String> args) {
        this.args = args;
    }
}
