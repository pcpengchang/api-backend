package com.pc.apiclientsdk.client;

import org.apache.logging.log4j.util.Strings;

/**
 * @author pengchang
 * @date 2023/08/02 22:02
 **/
public class SearchClient extends ApiClient {
    private static final String URL = "/search";

    public SearchClient(String accessKey, String secretKey) {
        super(accessKey, secretKey);
    }

    public String searchTags(String text) {
        return invoke(text, URL + "/getTags", true);
    }

    public String translate(String text) {
        return invoke(text, URL + "/translate", true);
    }

    public String getBaiduNews(String text) {
        return invoke(text, URL + "/getBaiduNews", true);
    }

    public String getHotComments() {
        return invoke(Strings.EMPTY, URL + "/getHotComments", true);
    }

    public String getInterestingText() {
        return invoke(Strings.EMPTY, URL + "/getInterestingText", true);
    }

    public String getWisdomText() {
        return invoke(Strings.EMPTY, URL + "/getWisdomText", true);
    }

    public String getWeatherInfo(String text) {
        return invoke(text, URL + "/getWeatherInfo", true);
    }
}
