package com.pc.apicommon.service;

import com.pc.apicommon.model.entity.User;


/**
 * 用户服务
 *
 * @author pengchang
 */
public interface InnerUserService {

    /**
     * 数据库中查是否已分配给用户秘钥（accessKey）
     * @param accessKey
     * @return
     */
    User getInvokeUser(String accessKey);
}
