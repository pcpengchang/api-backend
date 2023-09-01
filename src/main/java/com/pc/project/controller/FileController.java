package com.pc.project.controller;

import cn.hutool.core.io.FileUtil;
import com.pc.apicommon.model.entity.User;
import com.pc.project.common.BaseResponse;
import com.pc.project.common.ErrorCode;
import com.pc.project.exception.BusinessException;
import com.pc.project.service.UserService;
import com.pc.project.strategy.UploadStrategyService;
import com.pc.project.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author pengchang
 * @date 2023/08/06 22:00
 **/
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UploadStrategyService uploadStrategyService;

    @Resource
    private UserService userService;

    @PostMapping("/upload")
    public BaseResponse<String> updateUserAvatar(@RequestBody MultipartFile file,
                                                 HttpServletRequest request) {
        validFile(file);
        User loginUser = userService.getLoginUser(request);
        String url = uploadStrategyService.executeUploadStrategy(file, "avatar/");
        loginUser.setUserAvatar(url);
        userService.updateById(loginUser);
        return ResultUtils.success(url);
    }

    private void validFile(MultipartFile multipartFile) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (fileSize > 1024 * 1024L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
        }
        if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
        }
    }
}
