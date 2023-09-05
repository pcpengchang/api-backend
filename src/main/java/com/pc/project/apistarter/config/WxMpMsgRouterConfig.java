package com.pc.project.apistarter.config;

import com.pc.project.apidomain.message.wx.MsgHandler;
import com.pc.project.apidomain.message.wx.SubscribeHandler;
import com.pc.project.apidomain.message.wx.UnsubscribeHandler;
import me.chanjar.weixin.common.api.WxConsts.EventType;
import me.chanjar.weixin.common.api.WxConsts.XmlMsgType;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/08/05 00:49
 **/
@Configuration
public class WxMpMsgRouterConfig {
    @Resource
    private WxMpService wxMpService;

    @Resource
    private MsgHandler msgHandler;

    @Resource
    private UnsubscribeHandler unsubscribeHandler;

    @Resource
    private SubscribeHandler subscribeHandler;

    @Bean
    public WxMpMessageRouter getWxMsgRouter() {
        WxMpMessageRouter router = new WxMpMessageRouter(wxMpService);
        // 消息
        router.rule()
                .async(false)
                .msgType(XmlMsgType.TEXT)
                .handler(msgHandler)
                .end();
        // 关注
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE)
                .handler(subscribeHandler)
                .end();
        // 取关
        router.rule()
                .async(false)
                .msgType(XmlMsgType.EVENT)
                .event(EventType.UNSUBSCRIBE)
                .handler(unsubscribeHandler)
                .end();
        return router;
    }
}
