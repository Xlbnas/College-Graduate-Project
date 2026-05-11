package com.campus.trade.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 本地图片存储（论文 5.1.2：存储到本地服务器 /upload 目录）
 */
@Component
public class FileUploadUtil {

    @Value("${trade.upload-dir:./upload}")
    private String uploadDir;

    public String upload(MultipartFile file) throws IOException {
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf('.'));
        }
        String filename = IdUtil.fastSimpleUUID() + ext;
        Path dir = Paths.get(uploadDir).toAbsolutePath().normalize();
        FileUtil.mkdir(dir.toString());
        Path target = dir.resolve(filename);
        file.transferTo(target.toFile());
        return "/files/" + filename;
    }
}
