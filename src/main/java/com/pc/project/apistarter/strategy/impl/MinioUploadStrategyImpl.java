package com.pc.project.apistarter.strategy.impl;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.StatObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * todo 测试
 *
 * @author pengchang
 * @date 2023/09/02 21:50
 **/
@Slf4j
@Service
public class MinioUploadStrategyImpl extends AbstractUploadStrategyImpl {
    @Value("${upload.minio.url}")
    private String url;

    @Value("${upload.minio.endpoint}")
    private String endpoint;

    @Value("${upload.minio.accessKey}")
    private String accessKey;

    @Value("${upload.minio.secretKey}")
    private String secretKey;

    @Value("${upload.minio.bucketName}")
    private String bucketName;

    @Override
    public Boolean exists(String filePath) {
        StatObjectArgs statObjectArgs = StatObjectArgs.builder().bucket(bucketName).object(filePath).build();
        try {
            return getMinioClient().statObject(statObjectArgs) != null;
        } catch (Exception e) {
            log.error("minio 查询文件路径失败", e);
        }
        return false;
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        PutObjectArgs objectArgs = PutObjectArgs.builder()
                .bucket(bucketName)
                .object(path + fileName)
                .stream(inputStream, inputStream.available(), -1L)
                .build();
        try {
            getMinioClient().putObject(objectArgs);
        } catch (Exception e) {
            log.error("minio 上传文件失败", e);
        }
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return url + bucketName + "/"  + filePath;
    }

    @Override
    public boolean needProcess(String param) {
        return "minio".equals(param);
    }

    private MinioClient getMinioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
