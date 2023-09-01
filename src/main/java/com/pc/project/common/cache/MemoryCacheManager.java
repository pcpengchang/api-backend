package com.pc.project.common.cache;

import com.pc.apicommon.model.entity.InterfaceInfo;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author pengchang
 * @date 2023/08/05 23:01
 **/
public class MemoryCacheManager {
    public static final InterfaceInfo EMPTY_MODEL = new InterfaceInfo();

    public static final Optional<InterfaceInfo> EMPTY_OPTIONAL_MODEL = Optional.of(EMPTY_MODEL);

    /**
     * 接口基本信息缓存
     * key:接口id
     * value:InterfaceInfo
     */
    public static final ConcurrentMap<Long, Optional<InterfaceInfo>> BASE_INFO_CACHE = new ConcurrentHashMap<>();

    public static InterfaceInfo getInterfaceInfoCacheById(Long id) {
        return BASE_INFO_CACHE
                .getOrDefault(id, EMPTY_OPTIONAL_MODEL).orElse(EMPTY_MODEL);
    }

    public static void updateBaseInfo(Long id, InterfaceInfo cacheModel) {
        InterfaceInfo oldValue = getInterfaceInfoCacheById(id);
        //有差异才更新，保持老的大对象不被GC
        if (!Objects.equals(oldValue, cacheModel)) {
            BASE_INFO_CACHE.put(id, Optional.ofNullable(cacheModel));
        }
    }

    public static void cleanUpExpiredBaseInfo(Set<Long> latestKeys) {
        Set<Long> oldKeys = BASE_INFO_CACHE.keySet();
        oldKeys.stream().filter(oldKey -> !latestKeys.contains(oldKey)).forEach(BASE_INFO_CACHE::remove);
    }
}
