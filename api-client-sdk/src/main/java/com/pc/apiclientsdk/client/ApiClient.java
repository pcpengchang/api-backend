package com.pc.apiclientsdk.client;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Maps;
import com.pc.apiclientsdk.http.*;
import com.pc.apiclientsdk.utils.JsonUtil;
import com.pc.apiclientsdk.utils.SignUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 调用第三方接口的客户端
 *
 * @author pengchang
 */
@Slf4j
public class ApiClient {

    /**
     * 本地环境
     */
//    private static final String GATEWAY_HOST = "http://localhost:8090/api";
    private static final String GATEWAY_HOST = "localhost:8090";

    /**
     * 生产环境
     */
    //private static final String GATEWAY_HOST = "http://120.25.220.64:8090";

    private String accessKey;

    private String secretKey;

    private Map<String, String> getHeaderMap(String body) {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("accessKey", accessKey);
        hashMap.put("nonce", RandomUtil.randomNumbers(4));

        try {
            body = URLEncoder.encode(body, "utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(body, secretKey));
        log.info("body ===> {}", body);
        log.info("secretKey ===> {}", secretKey);
        return hashMap;
    }

    protected String invoke(String body, String url, boolean isPost) {
        // TODO 加入get方法
//        HttpResponse httpResponse = HttpRequest.post(GATEWAY_HOST + url)
//                .addHeaders(getHeaderMap(body))
//                .body(body)
//                .execute();
//        log.info("ApiClient 返回结果{}", httpResponse);
//        Result result = Result.builder()
//                .status(httpResponse.getStatus())
//                .body(httpResponse.body()).build();
//        return JSONUtil.toJsonStr(httpResponse.body());
        HttpServiceConfig config = new HttpServiceConfig();
        // 请求地址和方法
        config.setHost(GATEWAY_HOST);
        config.setPath("/api" + url);
        config.setHttpMethod(HttpMethod.POST);
        config.setProtocolType(HttpProtocol.HTTP);

        HttpDataSourceRequest request = new HttpDataSourceRequest();
        // 请求头
        request.setHttpHeaders(getHeaderMap(body));
        request.setRemoteIp(url);

        // 请求体
        request.setHttpBodyParam(JsonUtil.strToJsonNode(body));

        CompletableFuture<Object> resultFuture = DataSourceFactory.process(config, request);
        if (resultFuture == null) {
            log.error("invoke result future is null");
        } else if (resultFuture.isCompletedExceptionally()) {
            resultFuture.whenComplete((r, e) -> {
                if (e != null) {
                    log.error("invoke result future error", e);
                }
            });
        }

        Map<String, Object> moduleResponseMap = Maps.newConcurrentMap();

        resultFuture.thenAccept(result -> {
            moduleResponseMap.put(url, result);
        });

        try {
//            long timeOut = 2000L;
//            resultFuture.get(timeOut, TimeUnit.MILLISECONDS);
            resultFuture.get();
        } catch (Exception e) {
            log.error("invoke error", e);
        }

        log.info("invoke response {}", moduleResponseMap);
        return JSONUtil.toJsonStr(moduleResponseMap.get(url));
    }

    // 提供给第三方开发者的示例接口
    public String getUsernameByPost(String body) throws UnsupportedEncodingException {
        return invoke(body, "/name/user", true);
    }

    /**
     * 测试 begin
     **/
    public static void main(String[] args) {
        String body = "{\"searchText\": \"你的关键词\"}";
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("nonce", RandomUtil.randomNumbers(4));
        hashMap.put("body", body);
        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis() / 1000));
        hashMap.put("sign", SignUtils.genSign(body, "12"));
        log.info("sign ===> {}", hashMap.get("sign"));
        log.info("secretKey ===> {}", "12");
    }

    @Data
    class User {
        private Integer a;
        private int b;
    }

    public ApiClient(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    public User test3(User user) {
        System.out.println(user.getA());
        return user;
    }

    public List<String> test2(List<String> str) {
        System.out.println(str);
        return str;
    }

    public String test(String str, String str2) {
        System.out.println(str + str2);
        return str;
    }

    public String getUserNameByGet(String name) {
        //可以单独传入http参数，这样参数会自动做URL编码，拼接在URL中
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", name);
        String result = HttpUtil.get(GATEWAY_HOST + "/api/name/", paramMap);
        System.out.println(result);
        return result;
    }

    /** 测试 end **/

}
