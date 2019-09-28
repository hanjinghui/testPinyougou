package com.pinyougou.manager.controller;


/*
*  文键上传类
**/

import com.pinyougou.common.FastDFSClient;
import entity.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;

    @RequestMapping("/upload.do")
    public Result upload(MultipartFile file){

        //获取文件扩展名
        String filename = file.getOriginalFilename();
        String extName = filename.substring(filename.lastIndexOf(".") + 1);

        //接受文件将文件上传到fastDfs上
        try {
            FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");

            String fileId = client.uploadFile(file.getBytes(),extName);

            //拼接文件的真实路径

            String path = FILE_SERVER_URL+fileId;
           return  new Result(true,path);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
