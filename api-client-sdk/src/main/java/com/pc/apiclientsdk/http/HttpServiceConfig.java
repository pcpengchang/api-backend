package com.pc.apiclientsdk.http;

import lombok.Getter;
import lombok.Setter;

/**
 * @author pengchang
 * @date 2021/3/30
 */
@Getter
@Setter
public class HttpServiceConfig {

    private String host;

    private String path;

    private HttpProtocol protocolType;

    private HttpMethod httpMethod;
}
