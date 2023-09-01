package com.pc.apiclientsdk.http;

import java.util.concurrent.CompletableFuture;

/**
 * @author pengchang
 * @since 2023-10-26
 */
public interface AsyncRPCInvoker {
    <T> CompletableFuture<T> call(Object... params);
}
