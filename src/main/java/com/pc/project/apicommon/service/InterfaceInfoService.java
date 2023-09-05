package com.pc.project.apicommon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.pc.apicommon.model.entity.InterfaceInfo;

import java.util.List;

/**
 *
 */
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    void validInterfaceInfo(InterfaceInfo interfaceInfo, boolean add);

    /**
     * 根据接口描述，匹配其他相关接口
     */
    List<String> matchInterfaceInfos(long num, InterfaceInfo interfaceInfo);
}
