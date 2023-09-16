package com.pc.project.apistarter.utils;

import com.google.common.collect.Sets;
import com.pc.project.apistarter.strategy.RouterStrategy;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Set;

/**
 * 策略工具
 */
@UtilityClass
@Slf4j
public class StrategyUtils {

    /**
     * 获取唯一对应实现类
     * 注入所有对应策略的实现类，判断符合条件的实现类为唯一一个则返回，否则返回null，不进行兜底
     * 防止后续开发写出了重复的命中策略执行非预期中的逻辑
     */
    public static <T extends RouterStrategy, P> T getProcessService(P param, List<T> serviceList) {
        if (CollectionUtils.isEmpty(serviceList)) {
            return null;
        }
        Set<T> singleSet = Sets.newHashSetWithExpectedSize(serviceList.size());
        for (T service : serviceList) {
            if (service.needProcess(param)) {
                singleSet.add(service);
            }
        }
        //此处校验防止命中多个策略问题
        if (CollectionUtils.isEmpty(singleSet) || singleSet.size() > 1) {
            log.error("match strategy error");
            return null;
        }
        return singleSet.iterator().next();
    }
}
