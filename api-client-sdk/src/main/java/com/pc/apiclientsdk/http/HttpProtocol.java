package com.pc.apiclientsdk.http;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * @author pengchang
 * @date 2021/3/30
 */
@Getter
@AllArgsConstructor
public enum HttpProtocol {

    HTTP("http://"),
    HTTPS("https://");

    private String protocolPrefix;

    public static HttpProtocol find(String name){
        for (HttpProtocol httpProtocol : HttpProtocol.values()) {
            if (Objects.equals(httpProtocol.name(),name)) {
                return httpProtocol;
            }
        }
        return null;
    }

}
