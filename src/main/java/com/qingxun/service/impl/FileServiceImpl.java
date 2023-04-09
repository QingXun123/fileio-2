package com.qingxun.service.impl;

import com.qingxun.service.FileService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service("fileService")
public class FileServiceImpl implements FileService {

    /**
     * 检查分块文件是否存在
     * @param fileMd5 文件Md5值
     * @param fileId 分块文件编号
     * @return 块文件是否存在
     */
    public boolean checkChunk(String fileMd5, Integer fileId) {
        //定义分块文件存放路径
        String chunkFilePath = getChunkFilePath(fileMd5);
        //新建一个目录（文件夹）
        File dest = new File(chunkFilePath + fileId);
        // 判断文件是否存在
        if (!dest.exists()){
            // 不存在返回false
            return false;
        }
        // 存在则返回true
        return true;
    }

    /**
     * 下载分块文件
     * @param multipartFile 分块文件
     * @param fileMd5 文件Md5值
     * @param fileId 分块文件编号
     * @return 下载是否成功
     */
    public boolean uploadFile(MultipartFile multipartFile, String fileMd5, Integer fileId) {
        //判断文件是否为空 isEmpty
        if (multipartFile == null || multipartFile.isEmpty()){
            return false;
        }
        //定义分块文件存放路径
        String chunkFilePath = getChunkFilePath(fileMd5);
        //新建一个目录（文件夹）
        File dest = new File(chunkFilePath + fileId);
        if (!dest.getParentFile().canExecute()){
            dest.getParentFile().mkdirs();
        }
        try {
            //文件输出
            multipartFile.transferTo(dest);
        }
        catch (Exception e) {
            e.printStackTrace();
            //拷贝失败要有提示
            return false;
        }
        return true;
    }

    /**
     * 合并分块
     * @param fileName 新文件名字
     * @param sourceFileMd5 前端计算好的原文件的Md5值
     * @return 合并是否成功
     * @throws IOException
     */
    public boolean mergeFile(String fileName, String sourceFileMd5) throws IOException {
        // 分块文件目录
        File chunkFolder = new File(getChunkFilePath(sourceFileMd5));
        // 合并文件
        File mergeFile = new File(getFilePath(sourceFileMd5) + fileName);
        if (mergeFile.exists()) {
            mergeFile.delete();
        }
        // 创建新的合并文件
        mergeFile.createNewFile();
        // 用于写文件
        RandomAccessFile raf_write = new RandomAccessFile(mergeFile, "rw");
        // 指针指向文件顶端
        raf_write.seek(0);
        // 缓冲区
        byte[] b = new byte[1024];
        // 分块列表
        File[] fileArray = chunkFolder.listFiles();
        // 转成集合，便于排序
        List<File> fileList = Arrays.asList(fileArray);
        // 从小到大排序
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return Integer.parseInt(o1.getName()) - Integer.parseInt(o2.getName());
            }
        });
        // 合并文件
        for (File chunkFile : fileList) {
            RandomAccessFile raf_read = new RandomAccessFile(chunkFile, "rw");
            int len = -1;
            while ((len = raf_read.read(b)) != -1) {
                raf_write.write(b, 0, len);

            }
            raf_read.close();
        }
        raf_write.close();
        // 校验文件
        try (FileInputStream mergeFileStream = new FileInputStream(mergeFile)) {
            // 取出合并文件的md5进行比较
            String mergeFileMd5 = DigestUtils.md5Hex(mergeFileStream);
            if (sourceFileMd5.equals(mergeFileMd5)) {
                System.out.println("合并文件成功");
                deleteChunk(sourceFileMd5);
                return true;
            } else {
                System.out.println("合并文件失败");
                return false;
            }
        }
    }

    /**
     * 删除桶
     */
    private void deleteChunk(String fileMd5) {
        String chunkFilePath = getChunkFilePath(fileMd5);
        File directory = new File(chunkFilePath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            // 遍历删除chunk里所有的分块文件
            for(File file : files) {
                file.delete();
            }
        }
        // 删除chunk文件夹
        directory.delete();
    }

    /**
     * 根据Md5计算文件存放路径
     * @param fileMd5 文件的Md5值
     * @return 文件存放路径
     */
    private static String getFilePath(String fileMd5) {
        return "C:\\Users\\uploadfile\\" + fileMd5.substring(0, 1) + "\\" + fileMd5.substring(1, 2) + "\\" + fileMd5 + "\\";
    }

    /**
     * 根据Md5计算分块文件存放路径
     * @param fileMd5 文件的Md5值
     * @return 文件存放路径
     */
    private static String getChunkFilePath(String fileMd5) {
        return getFilePath(fileMd5) + "chunk\\";
    }
}
