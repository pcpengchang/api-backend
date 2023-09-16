package com.pc.project.apistarter.strategy.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @author pengchang
 * @date 2023/08/06 22:29
 **/
@Service
public class OssUploadStrategyImpl extends AbstractUploadStrategyImpl {
    /**
     * oss域名
     */
    @Value("${upload.oss.url}")
    private String url;

    /**
     * 终点
     */
    @Value("${upload.oss.endpoint}")
    private String endpoint;

    /**
     * 访问密钥id
     */
    @Value("${upload.oss.accessKeyId}")
    private String accessKeyId;

    /**
     * 访问密钥密码
     */
    @Value("${upload.oss.accessKeySecret}")
    private String accessKeySecret;

    /**
     * bucket名称
     */
    @Value("${upload.oss.bucketName}")
    private String bucketName;

    /**
     * https://help.aliyun.com/zh/oss/developer-reference/determine-whether-an-object-exists-1?spm=a2c4g.11186623.0.0.74cd6d31hNgHr2
     */
    @Override
    public Boolean exists(String filePath) {
        return getOssClient().doesObjectExist(bucketName, filePath);
    }

    /**
     * https://help.aliyun.com/zh/oss/developer-reference/simple-upload-11?spm=a2c4g.11186623.0.0.10b575527blwL0
     */
    @Override
    public void upload(String path, String fileName, InputStream inputStream) {
        getOssClient().putObject(bucketName, path + fileName, inputStream);
    }

    @Override
    public String getFileAccessUrl(String filePath) {
        return url + filePath;
    }

    /**
     * 获取ossClient
     *
     * @return {@link OSS} ossClient
     */
    private OSS getOssClient() {
        return new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    @Override
    public boolean needProcess(String param) {
        return "oss".equals(param);
    }
}
