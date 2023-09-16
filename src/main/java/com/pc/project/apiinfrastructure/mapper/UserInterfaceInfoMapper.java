package com.pc.project.apiinfrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pc.apicommon.model.entity.UserInterfaceInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);
}




