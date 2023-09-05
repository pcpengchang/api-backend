package com.pc.gateway;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.pc.apiclientsdk.utils.SignUtils;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.apicommon.model.entity.User;
import com.pc.apicommon.service.IInterfaceInfoService;
import com.pc.apicommon.service.IUserInterfaceInfoService;
import com.pc.apicommon.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR;

/**
 * 全局过滤
 */
@Slf4j
@Component
public class CustomGlobalFilter implements GlobalFilter, Ordered {

    @DubboReference
    private IUserService innerUserService;

    @DubboReference
    private IInterfaceInfoService innerInterfaceInfoService;

    @DubboReference
    private IUserInterfaceInfoService innerUserInterfaceInfoService;

    private static final List<String> IP_WHITE_LIST = Collections.singletonList("127.0.0.1");

    private static final String INTERFACE_HOST = "/api";

    private static Joiner joiner = Joiner.on("");

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 请求日志
        ServerHttpRequest request = exchange.getRequest();
//        String path = INTERFACE_HOST + request.getId();
        String method = Objects.requireNonNull(request.getMethod()).toString();
        log.info("请求唯一标识：" + request.getId());
        log.info("完整请求" + request.getPath());
        String path = request.getPath().toString();

        log.info("请求地址" + request.getRemoteAddress());
//        log.info("请求路径：" + path);
        log.info("请求方法：" + method);
        log.info("请求参数：" + request.getQueryParams());
        String sourceAddress = Objects.requireNonNull(request.getLocalAddress()).getHostString();
        log.info("请求来源地址：" + sourceAddress);
        log.info("请求来源地址：" + request.getRemoteAddress());
        ServerHttpResponse response = exchange.getResponse();
        // 2. 访问控制 - 黑白名单
        if (!IP_WHITE_LIST.contains(sourceAddress)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return response.setComplete();
        }
        // 3. 用户鉴权（判断 ak、sk 是否合法）
        HttpHeaders headers = request.getHeaders();
        String accessKey = headers.getFirst("accessKey");
        log.info("accessKey ===> {}", accessKey);
        String nonce = headers.getFirst("nonce");
        String timestamp = headers.getFirst("timestamp");
        String sign = headers.getFirst("sign");
        String body = headers.getFirst("body");
//        // 防止中文乱码
//        String body;
//        try {
//            body = URLDecoder.decode(Objects.requireNonNull(headers.getFirst("body")), StandardCharsets.UTF_8.name());
//        } catch (UnsupportedEncodingException e) {
//            throw new RuntimeException(e);
//        }

        // 去数据库中查是否已分配ak给用户
        User invokeUser = null;
        try {
            invokeUser = innerUserService.getInvokeUser(accessKey);
        } catch (Exception e) {
            log.error("getInvokeUser error", e);
        }
        if (invokeUser == null) {
            return handleNoAuth(response);
        }

        if (Long.parseLong(nonce) > 10000L) {
            return handleNoAuth(response);
        }
        // 时间和当前时间不能超过 5 分钟
        long currentTime = System.currentTimeMillis() / 1000;
        if ((currentTime - Long.parseLong(timestamp)) >= 60 * 5L) {
            return handleNoAuth(response);
        }

        // 从数据库中查出 secretKey
        String secretKey = invokeUser.getSecretKey();
        String serverSign = SignUtils.genSign(body, secretKey);
        log.info("sign       ===> {}", sign);
        log.info("serverSign ===> {}", serverSign);
        log.info("body       ===> {}", body);
        if (sign == null || !sign.equals(serverSign)) {
            log.info("签名不相等");
            return handleNoAuth(response);
        }

        // 4. 请求的模拟接口是否存在，以及请求方法是否匹配
        InterfaceInfo interfaceInfo = null;
        try {
            interfaceInfo = innerInterfaceInfoService.getInterfaceInfo(path);
            log.info("interfaceInfo ===> {}", interfaceInfo);
        } catch (Exception e) {
            log.error("getInterfaceInfo error", e);
        }

        if (interfaceInfo == null) {
            return handleNoAuth(response);
        }

        // todo 是否还有调用次数
        // 5. 先扣减调用次数
        try {
            innerUserInterfaceInfoService.reduceInvokeCount(interfaceInfo.getId(), invokeUser.getId());
        } catch (Exception e) {
            log.error("用户接口调用次数【扣减】异常", e);
            return handleNoAuth(response);
        }

        // 6. 请求转发，调用模拟接口 + 响应日志
        return handleResponse(exchange, chain, interfaceInfo.getId(), invokeUser.getId());

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String body = "{\"searchText\": \"你的关键词\"}";
        System.out.println(SignUtils.genSign(body, "12"));
        body = URLEncoder.encode(Objects.requireNonNull(body), StandardCharsets.UTF_8.name());
        System.out.println(SignUtils.genSign(body, "12"));
    }

    /**
     * 处理响应
     * 状态码不为200 or body形式不对 进行次数回滚
     *
     * @param exchange
     * @param chain
     * @return
     */
    public Mono<Void> handleResponse(ServerWebExchange exchange, GatewayFilterChain chain, long interfaceInfoId, long userId) {
        try {
            // 获取响应对象
            ServerHttpResponse originalResponse = exchange.getResponse();
            // 缓存数据的工厂
            DataBufferFactory bufferFactory = originalResponse.bufferFactory();
            // 拿到响应码
            HttpStatus statusCode = originalResponse.getStatusCode();
            log.info("handleResponse originalResponse.getStatusCode {}", statusCode);
            log.info("handleResponse originalResponse {}", originalResponse);
            if (statusCode == HttpStatus.OK) {
                // 装饰，增强能力
                ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
                    // 等调用完转发的接口后才会执行
                    @Override
                    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                        log.info("handleResponse getStatusCode {}", getStatusCode());
                        log.info("handleResponse body {}", body);
                        log.info("handleResponse body instanceof Flux: {}", (body instanceof Flux));

                        // 获取响应 ContentType  判断是否返回JSON格式数据
                        String responseContentType = exchange.getAttribute(ORIGINAL_RESPONSE_CONTENT_TYPE_ATTR);
                        if (HttpStatus.OK.equals(getStatusCode()) && body instanceof Flux
                                && StringUtils.isNotEmpty(responseContentType)) {
//                                && responseContentType.contains(MediaType.APPLICATION_JSON_VALUE)) {
                            Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                            //解决返回体分段传输
                            return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                                List<String> list = Lists.newArrayList();
                                dataBuffers.forEach(dataBuffer -> {
                                    try {
                                        byte[] content = new byte[dataBuffer.readableByteCount()];
                                        dataBuffer.read(content);
                                        //释放掉内存
                                        DataBufferUtils.release(dataBuffer);
                                        list.add(new String(content, StandardCharsets.UTF_8));
                                    } catch (Exception e) {
                                        log.error("返回体分段传输失败", e);
                                    }
                                });
                                String responseData = joiner.join(list);
                                byte[] uppedContent = new String(responseData.getBytes(), StandardCharsets.UTF_8).getBytes();
                                originalResponse.getHeaders().setContentLength(uppedContent.length);
                                log.info("响应结果：{}", uppedContent);
                                return bufferFactory.wrap(uppedContent);
                            }));
                        } else {
                            // 7. 调用失败，次数回滚
                            try {
                                innerUserInterfaceInfoService.rollbackInvokeCount(interfaceInfoId, userId);
                            } catch (Exception e) {
                                log.error("用户接口调用次数【回滚】异常", e);
                            }
                            // 8. 调用失败，返回一个规范的错误码
                            log.error("<--- {} 响应code异常", getStatusCode());
                        }
                        return super.writeWith(body);
                    }
                };
                // 设置 response 对象为装饰过的
                return chain.filter(exchange.mutate().response(decoratedResponse).build());
            } else {
                // 7. 调用失败，次数回滚
                try {
                    innerUserInterfaceInfoService.rollbackInvokeCount(interfaceInfoId, userId);
                } catch (Exception e) {
                    log.error("invokeCount error", e);
                }
            }
            // 降级处理返回数据
            return chain.filter(exchange);
        } catch (Exception e) {
            log.error("网关处理响应异常" + e);
            return chain.filter(exchange);
        }
    }

    @Override
    public int getOrder() {
        return -1000;
    }

    public Mono<Void> handleNoAuth(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.FORBIDDEN);
        return response.setComplete();
    }

    public Mono<Void> handleInvokeError(ServerHttpResponse response) {
        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
        return response.setComplete();
    }
}