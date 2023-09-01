package com.pc.apiclientsdk.http;

import lombok.Data;

import java.util.concurrent.CompletableFuture;

/**
 * @author pengchang
 * @date 2023/08/29 21:42
 **/
@Data
public class DataSource<Request, Response> {
    private AsyncRPCInvoker invoker;

    public CompletableFuture<Response> call(Request request) {
        CompletableFuture<Response> responseFuture;

        if (request == null) {
            responseFuture = invoker.call((Object) null);
        } else if (request.getClass().isArray()) {
            responseFuture = invoker.call((Object[]) request);
        } else {
            responseFuture = invoker.call(request);
        }
        return responseFuture;
    }
}
