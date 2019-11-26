package com.atguigu.guli.service.oss.service;

import java.io.InputStream;

/**
 * @author eternity
 * @create 2019-11-25 20:45
 */
public interface FileService {

    /**
     * 文件上传
     * @param inputStream 输入流
     * @param module 模块名
     * @param originalFilename 文件原始名称
     * @return 文件在oss上的URL地址
     */
    String upload(InputStream inputStream, String module, String originalFilename);

    void removeFile(String url);
}
