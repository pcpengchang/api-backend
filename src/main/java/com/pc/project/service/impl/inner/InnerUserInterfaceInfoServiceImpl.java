package com.pc.project.service.impl.inner;

import com.pc.apicommon.service.InnerUserInterfaceInfoService;
import com.pc.project.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public boolean reduceInvokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.reduceInvokeCount(interfaceInfoId, userId);
    }

    @Override
    public boolean rollbackInvokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.rollbackInvokeCount(interfaceInfoId, userId);
    }
}
