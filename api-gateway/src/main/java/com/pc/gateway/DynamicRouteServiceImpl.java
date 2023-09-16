package com.pc.gateway;

import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/09/06 11:02
 **/
@Service
public class DynamicRouteServiceImpl implements ApplicationEventPublisherAware {

    @Resource
    private RouteDefinitionWriter routeDefinitionWriter;


    @Resource
    private ApplicationEventPublisher publisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    //新增路由
    public void add(MyRouteDefinition myRouteDefinition){
        /**
         * 新增的Actuator Endpoint，刷新路由的时候，先加载路由配置到内存中，
         * 然后再使用RefreshRoutesEvent事件刷新内存中路由配置。
         */
        routeDefinitionWriter.save(Mono.just(myRouteDefinition.getRouteDefinition())).subscribe();
        this.publisher.publishEvent(new RefreshRoutesEvent(this));
    }

    // 删除路由
    public void delete(String id){
        routeDefinitionWriter.delete(Mono.just(id)).subscribe();
    }
}
