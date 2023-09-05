package com.pc.project.apitrigger.rpc;

import com.pc.apicommon.service.IUserInterfaceInfoService;
import com.pc.project.apicommon.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class IUserInterfaceInfoServiceImpl implements IUserInterfaceInfoService {

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
