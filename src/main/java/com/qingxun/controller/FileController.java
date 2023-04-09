package com.qingxun.controller;

import com.qingxun.service.FileService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("file")
@Api(tags = "文件操作")
public class FileController {

    @Autowired
    private FileService fileService;

    @ApiOperation("分块文件检查")
    @GetMapping("/checkfile")
    public Boolean checkChunk(String fileMd5, Integer fileId) {
        return fileService.checkChunk(fileMd5, fileId);
    }

    @ApiOperation("文件上传")
    @PostMapping("/uploadfile")
    public Boolean uploadFile(MultipartFile multipartFile, String fileMd5, Integer fileId) {
        return fileService.uploadFile(multipartFile, fileMd5, fileId);
    }

    @ApiOperation("文件合并")
    @GetMapping("/mergefile")
    public Boolean mergeFile(String fileName, String fileMd5) {
        try {
            return fileService.mergeFile(fileName, fileMd5);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

}
