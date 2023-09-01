package com.pc.apiclientsdk.http;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;

import java.util.concurrent.CompletableFuture;

/**
 * @author pengchang
 * @date 2023/08/29 20:51
 **/
@Slf4j
public class DataSourceFactory {
    public static CompletableFuture<Object> process(HttpServiceConfig config, HttpDataSourceRequest request)  {
        HttpCallAsyncInvoker invoker = new HttpCallAsyncInvoker();
        invoker.setHost(config.getHost());
        invoker.setHttpMethod(config.getHttpMethod());
        invoker.setPath(config.getPath());
        invoker.setProtocolType(config.getProtocolType());
        invoker.setHttpClient(new OkHttpClient());

        DataSource<Object, Object> rootDataSource = new DataSource<>();
        rootDataSource.setInvoker(invoker);

        long startTime = System.currentTimeMillis();
        CompletableFuture<Object> responseCompletableFuture = rootDataSource.call(request);
        responseCompletableFuture.thenRun(() -> {
            long duration = System.currentTimeMillis() - startTime;
            log.info("远程请求耗时 {}", duration);
        });
        return responseCompletableFuture;
    }
}
