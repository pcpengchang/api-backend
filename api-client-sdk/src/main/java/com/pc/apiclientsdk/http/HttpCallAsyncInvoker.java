package com.pc.apiclientsdk.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


/**
 * @author pengchang
 * @date 2023/08/29 20:38
 **/
@Getter
@Setter
public class HttpCallAsyncInvoker implements AsyncRPCInvoker {
    private static final List<String> REMOVE_HEADER_KEY = Collections.unmodifiableList(
            Lists.newArrayList("Content-Length", "Host", "Accept-Encoding"));

    private static final String MEDIA_TYPE = "application/json";

    private static final String X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String X_FORWARDED_FOR_SEPARATE = ", ";

    private OkHttpClient httpClient = new OkHttpClient.Builder().build().newBuilder()
            .connectTimeout(50, TimeUnit.SECONDS)
            .readTimeout(50, TimeUnit.SECONDS)
            .writeTimeout(50, TimeUnit.SECONDS)
            .build();

    private String host;

    private String path;

    private HttpProtocol protocolType;

    private HttpMethod httpMethod;

    private static String parseParams(Map<String, String> params) {
        return Joiner.on("&")
                .useForNull("")
                .withKeyValueSeparator("=")
                .join(params);
    }

    private static String parseParams(JsonNode moduleParams) {
        Iterator<Map.Entry<String, JsonNode>> jsonNodes = moduleParams.fields();
        Map<String, String> paramsMap = Maps.newHashMapWithExpectedSize(moduleParams.size());
        while (jsonNodes.hasNext()) {
            Map.Entry<String, JsonNode> node = jsonNodes.next();
            paramsMap.put(node.getKey(), node.getValue().asText());
        }
        return parseParams(paramsMap);
    }

    private static Headers parseHeaders(Map<String, String> headerParam, String remoteIp) {
        if (headerParam == null || headerParam.isEmpty()) {
            return null;
        }
        Headers.Builder headersBuilder = new Headers.Builder();
        headerParam.forEach((k, v) -> {
            if (REMOVE_HEADER_KEY.contains(k) || k.equals(X_FORWARDED_FOR)) {
                return;
            }
            headersBuilder.add(k, v);
        });
        String xForwardedFor;
        if (StringUtils.isNotBlank(headerParam.get(X_FORWARDED_FOR))) {
            xForwardedFor = headerParam.get(X_FORWARDED_FOR) + X_FORWARDED_FOR_SEPARATE + remoteIp;
        } else {
            xForwardedFor = remoteIp;
        }
        headersBuilder.add(X_FORWARDED_FOR, xForwardedFor);
        return headersBuilder.build();
    }

    private static RequestBody parseBody(JsonNode bodyParam) {
        if (bodyParam == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse(MEDIA_TYPE), bodyParam.toString());
    }

    @Override
    public <T> CompletableFuture<T> call(Object... params) {
        HttpDataSourceRequest httpRequest = (HttpDataSourceRequest) params[0];
        StringBuilder url = new StringBuilder();
        url.append(protocolType.getProtocolPrefix()).append(host).append(path);

        JsonNode moduleParam = null;
        Headers headers = null;
        if (httpRequest != null) {
            moduleParam = httpRequest.getHttpBodyParam();
            headers = parseHeaders(httpRequest.getHttpHeaders(), httpRequest.getRemoteIp());
        }
        Request request;
        switch (httpMethod) {
            case POST:
                request = new Request.Builder()
                        .url(url.toString())
                        .post(parseBody(moduleParam))
                        .headers(headers)
                        .build();
                break;
            case GET:
                if (moduleParam != null && moduleParam.size() != 0) {
                    url.append("?").append(parseParams(moduleParam));
                }
                request = new Request.Builder()
                        .url(url.toString())
                        .get()
                        .headers(headers)
                        .build();
                break;
            default:
                request = new Request.Builder()
                        .url(url.toString())
                        .headers(headers)
                        .build();
        }
        Call call = httpClient.newCall(request);
        CompletableFuture completableFuture = new CompletableFuture();
        Callback callback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                completableFuture.completeExceptionally(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    completableFuture.complete(response.body().string());
                } catch (IOException e) {
                    completableFuture.completeExceptionally(e);
                }
            }
        };
        call.enqueue(callback);
        return completableFuture;
    }
}
