package com.pc.project.handler;


import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pc.apicommon.model.entity.User;
import com.pc.project.mapper.UserMapper;
import com.pc.project.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

@Slf4j
@Component
public class MsgHandler implements WxMpMessageHandler {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    private static final long LOGIN_EXPIRE_TIME = 60 * 10;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        if (StringUtils.startsWithAny(wxMessage.getContent(), "登录", "登陆")) {
            String openId = wxMessage.getFromUser();
            log.info("新关注用户 OPENID: " + openId);

            // 查询用户是否存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", openId);
            User user = userMapper.selectOne(queryWrapper);

            //TODO 组装回复消息
            String code = RandomUtil.randomNumbers(6);
            redisService.set(code, user, LOGIN_EXPIRE_TIME);
            String content = String
                    .format("动态码：%s\n"
                            + "请在十分钟内登录 🕑", code);

            return WxMpXmlOutMessage.TEXT().content(content)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        }

        return WxMpXmlOutMessage.TEXT().content("感谢关注")
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .build();
    }

}
