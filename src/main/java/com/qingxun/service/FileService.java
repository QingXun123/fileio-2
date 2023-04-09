package com.qingxun.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {

    /**
     * 检查分块文件
     * @param fileMd5 文件Md5值
     * @param fileId 分块编号
     * @return 是否成功
     */
    public boolean checkChunk(String fileMd5, Integer fileId);

    /**
     * 上传分块文件
     * @param multipartFile 分块文件
     * @param fileMd5 文件Md5值
     * @param fileId 分块编号
     * @return 是否成功
     */
    public boolean uploadFile(MultipartFile multipartFile, String fileMd5, Integer fileId);

    /**
     * 合并分块文件
     * @param originalFilename 文件名
     * @param fileMd5 文件Md5值
     * @return 是否成功
     */
    public boolean mergeFile(String originalFilename, String fileMd5) throws IOException;
}
