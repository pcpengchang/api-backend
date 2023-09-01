package com.pc.apiclientsdk.http;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author pengchang
 * @date 2021/3/30
 */
@Getter
@Setter
public class HttpDataSourceRequest {

    /**
     * header 参数
     */
    private Map<String, String> httpHeaders;

    /**
     * module参数,post请求的body，get请求的param
     */
    private JsonNode httpBodyParam;

    /**
     * 远程访问ip，用于拼接X-Forwarded-For
     */
    private String remoteIp;
}
