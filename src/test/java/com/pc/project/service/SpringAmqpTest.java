//package com.pc.project.service;
//
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessageBuilder;
//import org.springframework.amqp.rabbit.connection.CorrelationData;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import javax.annotation.Resource;
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
///**
// * @author pengchang
// * @date 2023/08/05 16:55
// **/
//@Slf4j
//@SpringBootTest
//public class SpringAmqpTest {
//
//    @Resource
//    private RabbitTemplate rabbitTemplate;
//
//    @Test
//    public void testSimpleQueue() {
//        // 1.消息体
//        // 2.全局唯一的消息ID，需要封装到CorrelationData中
//        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
//        // 3.添加callback
//        correlationData.getFuture().addCallback(
//                result -> {
//                    if (result.isAck()) {
//                        // 3.1.ack，消息成功
//                        log.debug("消息发送成功, ID:{}", correlationData.getId());
//                    } else {
//                        // 3.2.nack，消息失败
//                        log.error("消息发送失败, ID:{}, 原因{}", correlationData.getId(), result.getReason());
//                    }
//                },
//                ex -> log.error("消息发送异常, ID:{}, 原因{}", correlationData.getId(), ex.getMessage())
//        );
//        // 交换机名称
//        String exchangeName = "exchange";
//        // 消息
//        Message message = MessageBuilder.withBody("你好！".getBytes(StandardCharsets.UTF_8))
//                .setHeader("x-delay", 10000).build();
//        // 发送消息
//        rabbitTemplate.convertAndSend(exchangeName, "key", message, correlationData);
//    }
//}
