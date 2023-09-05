package com.pc.project.apidomain.interfaceinfo;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pc.apicommon.model.entity.UserInterfaceInfo;
import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apiinfrastructure.mapper.UserInterfaceInfoMapper;
import com.pc.project.apicommon.service.UserInterfaceInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 *
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoMapper userInterfaceInfoMapper;

    @Override
    public void validUserInterfaceInfo(UserInterfaceInfo userInterfaceInfo, boolean add) {
        if (userInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 创建时，所有参数必须非空
        if (add) {
            if (userInterfaceInfo.getInterfaceInfoId() <= 0 || userInterfaceInfo.getUserId() <= 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口或用户不存在");
            }
        }
        if (userInterfaceInfo.getLeftNum() < 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "剩余次数不能小于 0");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean reduceInvokeCount(long interfaceInfoId, long userId) {
        // 判断
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LambdaQueryWrapper<UserInterfaceInfo> eq = new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoMapper.selectList(eq);

        // 用户目前调用次数
        int userInvokeNum = 0;
        if (CollectionUtils.isEmpty(userInterfaceInfos)) {
            UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
            userInterfaceInfo.setUserId(userId);
            userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
            userInterfaceInfo.setTotalNum(0);
            userInterfaceInfo.setLeftNum(0);
            userInterfaceInfo.setStatus(0);
            userInterfaceInfo.setIsDelete(0);
            userInterfaceInfo.setCreateTime(new Date());
            userInterfaceInfo.setUpdateTime(new Date());
            userInterfaceInfoMapper.insert(userInterfaceInfo);
        }
        else {
            userInvokeNum = userInterfaceInfos.get(0).getTotalNum();
        }

        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getTotalNum, userInvokeNum);

//        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum - 1, totalNum = totalNum + 1");
        return this.update(updateWrapper);
    }

    @Override
    public boolean rollbackInvokeCount(long interfaceInfoId, long userId) {
        LambdaQueryWrapper<UserInterfaceInfo> eq = new LambdaQueryWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId);
        List<UserInterfaceInfo> userInterfaceInfos = userInterfaceInfoMapper.selectList(eq);
        int userInvokeNum = userInterfaceInfos.get(0).getTotalNum();

        LambdaUpdateWrapper<UserInterfaceInfo> updateWrapper = new LambdaUpdateWrapper<UserInterfaceInfo>()
                .eq(UserInterfaceInfo::getUserId, userId)
                .eq(UserInterfaceInfo::getInterfaceInfoId, interfaceInfoId)
                .eq(UserInterfaceInfo::getTotalNum, userInvokeNum);

//        updateWrapper.gt("leftNum", 0);
        updateWrapper.setSql("leftNum = leftNum + 1, totalNum = totalNum - 1");
        return this.update(updateWrapper);
    }

}




