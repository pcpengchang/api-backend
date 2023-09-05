package com.pc.apicommon.service;

import com.pc.apicommon.model.entity.InterfaceInfo;

/**
 *
 */
public interface IInterfaceInfoService {

    InterfaceInfo getInterfaceInfoById(Integer id);

    /**
     * 从数据库中查询模拟接口是否存在（请求路径、请求方法、请求参数）
     */
    InterfaceInfo getInterfaceInfo(String path);

    /**
     * 统计接口调用次数
     *
     * @param userId
     * @param interfaceInfoId
     * @return
     */
    boolean invokeCount(long userId, long interfaceInfoId);
}
