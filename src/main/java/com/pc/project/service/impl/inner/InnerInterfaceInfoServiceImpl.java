package com.pc.project.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pc.apicommon.model.entity.InterfaceInfo;
import com.pc.apicommon.service.InnerInterfaceInfoService;
import com.pc.project.common.ErrorCode;
import com.pc.project.exception.BusinessException;
import com.pc.project.mapper.InterfaceInfoMapper;
import com.pc.project.service.UserInterfaceInfoService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Override
    public InterfaceInfo getInterfaceInfoById(Integer id) {
        return interfaceInfoMapper.selectById(id);
    }

    @Override
    public InterfaceInfo getInterfaceInfo(String path) {
        if (StringUtils.isBlank(path)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", path);
        List<InterfaceInfo> interfaceInfos = interfaceInfoMapper.selectList(queryWrapper);
        if (CollectionUtils.isEmpty(interfaceInfos)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return interfaceInfoMapper.selectList(queryWrapper).get(0);
        // return interfaceInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean invokeCount(long userId, long interfaceInfoId) {
        return false;
    }
}
