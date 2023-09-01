package com.pc.apiclientsdk.client;

/**
 * @author pengchang
 * @date 2023/08/02 17:46
 **/
public class AIClient extends ApiClient {
    private static final String URL = "/ai";

    public AIClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    public String doChat(String text) {
        return invoke(text, URL + "/chat", false);
    }

    public String genChart(String text) {
        return invoke(text, URL + "/genChart", false);
    }
}
