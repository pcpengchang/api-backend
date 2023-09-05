package com.pc.project.apistarter.strategy;

import com.pc.project.apicommon.response.ErrorCode;
import com.pc.project.apistarter.exception.BusinessException;
import com.pc.project.apistarter.enums.FileExtEnum;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

/**
 * @author pengchang
 * @date 2023/08/06 22:37
 **/
@Service
public class LocalUploadStrategyImpl extends AbstractUploadStrategyImpl {

    /**
     * 本地路径
     */
    @Value("${upload.local.path}")
    private String localPath;

    /**
     * 访问url
     */
    @Value("${upload.local.url}")
    private String localUrl;

    @Override
    public Boolean exists(String filePath) {
        return new File(localPath + filePath).exists();
    }

    @Override
    public void upload(String path, String fileName, InputStream inputStream) throws IOException {
        // 判断目录是否存在
        File directory = new File(localPath + path);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建目录失败");
            }
        }
        // 写入文件
        File file = new File(localPath + path + fileName);
        String ext = "." + fileName.split("\\.")[1];
        switch (Objects.requireNonNull(FileExtEnum.getFileExt(ext))) {
            // 缓冲流  一般用 BufferedInputStream 包装流
            case MD:
            case TXT:
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                while (reader.ready()) {
                    writer.write((char) reader.read());
                }
                writer.flush();
                writer.close();
                reader.close();
                break;
            default:
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file.toPath()));
                byte[] bytes = new byte[1024];
                int length;
                while ((length = bis.read(bytes)) != -1) {
                    bos.write(bytes, 0, length);
                }
                bos.flush();
                bos.close();
                bis.close();
                break;
        }
        inputStream.close();
    }


    @Override
    public String getFileAccessUrl(String filePath) {
        return localUrl + filePath;
    }

    @Override
    public boolean needProcess(String param) {
        return "local".equals(param);
    }
}