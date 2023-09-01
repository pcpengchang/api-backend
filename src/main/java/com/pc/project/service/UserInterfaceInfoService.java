package com.pc.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pc.apicommon.model.entity.UserInterfaceInfo;

/**
 *
 */
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {
    void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add);

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean reduceInvokeCount(long interfaceInfoId, long userId);

    boolean rollbackInvokeCount(long interfaceInfoId, long userId);
}
