//package com.pc.project.app.aop.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.amqp.rabbit.annotation.Exchange;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.QueueBinding;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
//import org.springframework.amqp.support.converter.MessageConverter;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author pengchang
// * @date 2023/08/05 17:01
// **/
//@Slf4j
//@Configuration
//public class RabbitMqConfig {
////    @Bean
////    public MessageConverter jsonMessageConverter(){
////        return new Jackson2JsonMessageConverter();
////    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "direct.queue1"),
//            exchange = @Exchange(name = "exchange", delayed = "true"),
//            key = {"key"}
//    ))
//    public void listenDirectQueue1(String msg){
//        System.out.println("消费者接收到direct.queue1的消息：【" + msg + "】");
//    }
//}
