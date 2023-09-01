package com.pc.apiclientsdk.http;

import java.util.Objects;

/**
 * @author pengchang
 * @date 2021/3/30
 */
public enum HttpMethod {

    GET,
    POST;

    public static HttpMethod find(String name){
        for (HttpMethod httpMethod : HttpMethod.values()) {
            if (Objects.equals(httpMethod.name(),name)) {
                return httpMethod;
            }
        }
        return null;
    }
}
