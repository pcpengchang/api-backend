package com.pc.project.apiinfrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pc.apicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @Entity com.pc.project.apistarter.model.entity.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




