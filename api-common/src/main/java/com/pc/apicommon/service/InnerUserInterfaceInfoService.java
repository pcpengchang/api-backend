package com.pc.apicommon.service;

/**
 *
 */
public interface InnerUserInterfaceInfoService {

    /**
     * 调用接口统计
     * @param interfaceInfoId
     * @param userId
     * @return
     */
    boolean reduceInvokeCount(long interfaceInfoId, long userId);

    boolean rollbackInvokeCount(long interfaceInfoId, long userId);
}
