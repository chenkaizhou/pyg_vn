package com.xyxy.core.controller;

import com.xyxy.core.pojo.entity.Result;
import com.xyxy.core.util.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {
    //读取application.properties配置文件内容，通过里面的key, FILE_SERVER_URL读取值并且赋值给FILE_SERVER这个变量
    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER;

    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file){
        try {
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            //上传文件返回文件保存的路径和文件名
            String path =  fastDFSClient.uploadFile(file.getBytes(), file.getOriginalFilename(), file.getSize());
            return new Result(true,FILE_SERVER+path);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"上传失败！");
        }
    }
}
