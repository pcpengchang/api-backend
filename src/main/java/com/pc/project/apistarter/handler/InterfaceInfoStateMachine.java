package com.pc.project.apistarter.handler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.pc.project.apistarter.enums.InterfaceInfoStatusEnum;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * @author pengchang
 * @date 2023/09/09 13:49
 **/
public class InterfaceInfoStateMachine {
    public static final Map<InterfaceInfoStatusEnum, Set<InterfaceInfoStatusEnum>> STATUS_MAP = Maps.newHashMap();

    static {
        STATUS_MAP.put(InterfaceInfoStatusEnum.OFFLINE, ImmutableSet.of(InterfaceInfoStatusEnum.ONLINE));
        STATUS_MAP.put(InterfaceInfoStatusEnum.ONLINE, ImmutableSet.of(InterfaceInfoStatusEnum.OFFLINE));
    }

    public static boolean isValid(InterfaceInfoStatusEnum fromStatus, InterfaceInfoStatusEnum toStatus) {
        Set<InterfaceInfoStatusEnum> toStatusSet = InterfaceInfoStateMachine.STATUS_MAP.get(fromStatus);
        return CollectionUtils.isNotEmpty(toStatusSet) && toStatusSet.contains(toStatus);
    }
}
