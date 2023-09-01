package com.pc.project.common.cache;

import com.google.common.collect.Sets;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.project.mapper.InterfaceInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 接口信息缓存加载器，负责初始化、定时刷新以及重试等各种加载动作
 *
 * @author pengchang
 * @date 2023/08/05 22:53
 **/
@Component
@Slf4j
@Order(Integer.MIN_VALUE)
public class MemoryCacheLoader implements ApplicationListener<ContextRefreshedEvent> {
    //缓存重载定时器
    private final ScheduledThreadPoolExecutor refreshExecutor = new ScheduledThreadPoolExecutor(1);

    //缓存重载失败定时器
    private final ScheduledThreadPoolExecutor retryExecutor = new ScheduledThreadPoolExecutor(1);

    //缓存重载间隔
    private final Long REFRESH_PERIOD = 10 * 60 * 1000L;
    //缓存重载失败重试间隔
    private final Long RETRY_PERIOD = 60 * 1000L;
    //缓存是否需要重载标识
    private final AtomicBoolean needRetryFlag = new AtomicBoolean(false);

    //缓存内容重载后，变化百分比xx.xx%，输出格式化器
    private final static NumberFormat PERCENT_FORMATTER = NumberFormat.getInstance();

    static {
        PERCENT_FORMATTER.setMaximumFractionDigits(2);
    }

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    public boolean loadAllCache() {
        try {
            Set<Long> newBasicInfoCacheKeys = Sets.newHashSet();
            // todo 只查id
            List<InterfaceInfo> interfaceInfos = interfaceInfoMapper.selectList(null);
            for (InterfaceInfo interfaceInfo : interfaceInfos) {
                //更新基本信息缓存，并记录本次更新的key用于清除旧缓存
                Long id = interfaceInfo.getId();
                MemoryCacheManager.updateBaseInfo(id, interfaceInfo);
                newBasicInfoCacheKeys.add(id);
            }
            //根据数据库构建结果，统一清理缓存中应不存在的key
            MemoryCacheManager.cleanUpExpiredBaseInfo(newBasicInfoCacheKeys);
            return true;
        } catch (Exception ex) {
            log.error("接口本地缓存构建失败", ex);
            needRetryFlag.compareAndSet(false, true);
            return false;
        }
    }

    private void retryLoadAllCache() {
        if (needRetryFlag.get() && loadAllCache()) {
            needRetryFlag.compareAndSet(true, false);
        }
    }

    /**
     * 监听容器加载完成，进行首次缓存加载
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        //因为使用springmvc有两个容器，这个方法会执行两次，要想只执行一次通过这个判断即可
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        refreshExecutor.scheduleAtFixedRate(this::loadAllCache, 0, REFRESH_PERIOD, TimeUnit.MILLISECONDS);
        retryExecutor.scheduleAtFixedRate(this::retryLoadAllCache, 0, RETRY_PERIOD, TimeUnit.MILLISECONDS);
    }
}
