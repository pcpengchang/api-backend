package com.pc.project.apitrigger.rpc;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pc.apicommon.model.entity.User;
import com.pc.apicommon.service.IUserService;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apiinfrastructure.mapper.UserMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

@DubboService
public class IUserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        return userMapper.selectOne(queryWrapper);
    }
}
