package com.pc.project.apistarter.strategy;

import com.pc.project.apistarter.utils.StrategyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author pengchang
 * @date 2023/08/06 22:02
 **/
@Slf4j
@Service
public class UploadStrategyService {
    /**
     * 上传模式
     */
    @Value("${upload.mode}")
    private String uploadMode;

    @Resource
    private List<UploadStrategy> uploadStrategyList;

    /**
     * 执行上传策略
     *
     * @param file 文件
     * @param path 路径
     * @return {@link String} 文件地址
     */
    public String executeUploadStrategy(MultipartFile file, String path) {
        UploadStrategy uploadStrategy = StrategyUtils.getProcessService(uploadMode, uploadStrategyList);
        if (uploadStrategy == null) {
            log.error("no match UploadStrategy");
            return null;
        }
        return uploadStrategy.uploadFile(file, path);
    }
}
