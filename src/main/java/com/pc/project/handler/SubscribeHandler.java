package com.pc.project.handler;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pc.apicommon.model.entity.User;
import com.pc.project.mapper.UserMapper;
import com.pc.project.service.RedisService;
import com.pc.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Component
public class SubscribeHandler implements WxMpMessageHandler {

    @Resource
    private UserService userService;

    @Resource
    private UserMapper userMapper;

    @Resource
    private RedisService redisService;

    private static final long LOGIN_EXPIRE_TIME = 60 * 10;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) throws WxErrorException {

        String openId = wxMessage.getFromUser();
        log.info("新关注用户 OPENID: " + openId);

        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", openId);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            String password = RandomUtil.randomNumbers(8);
            userService.userRegister(openId, password, password);
        }

        // 获取微信用户基本信息 todo 需要微信官方认证
        // https://mp.weixin.qq.com/advanced/advanced?action=table&token=1564493366&lang=zh_CN
        try {
            WxMpUser userWxInfo = weixinService.getUserService()
                    .userInfo(wxMessage.getFromUser(), null);
            log.info("userWxInfo {}", userWxInfo);
            if (userWxInfo != null) {
                // TODO 可以添加关注用户到本地数据库
            }
        } catch (WxErrorException e) {
            if (e.getError().getErrorCode() == 48001) {
                log.info("没有获取用户信息权限！");
            }
        }

        String code = RandomUtil.randomNumbers(6);
        redisService.set(code, user, LOGIN_EXPIRE_TIME);

        String content = String
                .format(
                        "感谢关注PC社区 ✨\n"
                                + "动态码：%s\n"
                                + "请在十分钟内登录 🕑\n"
                                + "或直接访问：<a href=\"%s\">PC社区</a>\n"
                                + "代码已开源：<a href=\"%s\">欢迎star</a> ⭐",
                        code, "http://120.25.220.64", "https://github.com/pcpengchang");

        try {
            return WxMpXmlOutMessage.TEXT().content(content)
                    .fromUser(wxMessage.getToUser())
                    .toUser(wxMessage.getFromUser())
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }
}
