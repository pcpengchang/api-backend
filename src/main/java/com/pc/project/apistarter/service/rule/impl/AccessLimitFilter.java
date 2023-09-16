package com.pc.project.apistarter.service.rule.impl;

import com.pc.apicommon.model.entity.User;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.annotation.LogicStrategy;
import com.pc.project.apistarter.enums.LogicModelEnum;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.service.rule.ILogicFilter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author pengchang
 * @date 2023/09/16 21:24
 **/
@Slf4j
@Component
@LogicStrategy(logicMode = LogicModelEnum.ACCESS_LIMIT)
public class AccessLimitFilter implements ILogicFilter {
    @Resource
    private RedissonClient redissonClient;

    @Override
    public void filter(User user) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = redissonClient.getRateLimiter("invoke_" + user.getId());
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求过快");
        }
    }
}
