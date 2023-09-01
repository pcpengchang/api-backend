package com.pc.project.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author pengchang
 * @date 2023/08/06 22:05
 **/
public interface UploadStrategy extends RouterStrategy<String> {
    String uploadFile(MultipartFile file, String path);
}
