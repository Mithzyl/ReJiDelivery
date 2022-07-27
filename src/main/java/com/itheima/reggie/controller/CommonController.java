package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/common")
public class CommonController {
     /*
       文件上传和下载
     */
    @Value("${reggie.path}")
    private String basePath;

    @PostMapping("/upload")
    public R<String> upload(MultipartFile file) throws IOException {
        // file是临时文件 需要进行转存 否则请求完成后会删除

        // 原始文件名 可能会存在重复名被覆盖问题
        String originalFileName = file.getOriginalFilename();

        // 获取后缀
        String substring = originalFileName.substring(originalFileName.lastIndexOf("."));

        // 使用UUId重新生成文件名
        String newFileName = UUID.randomUUID().toString() + substring;

        File dir = new File(basePath);
        // 目录是否存在 不存在则创建
        if(!dir.exists()){
            // 创建目录
            dir.mkdirs();
        }

        try{
            file.transferTo(new File(basePath + originalFileName));
        } catch (Exception e){
            log.info("上传失败 ", e);
        }

        return R.success(newFileName);
    }

    // 文件下载
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response){
        // 输入流 通过输入读取文件内容
        try {
            FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));

            // 输出流 通过输出流文件写回浏览器
            ServletOutputStream outputStream = response.getOutputStream();

            response.setContentType("/image/jpeg");

            byte[] bytes = new byte[1024];

            int len = 0;


            while((len = fileInputStream.read(bytes)) != -1 ){
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();
        }catch (Exception e){
            log.info("下载失败", e);
        }



    }


}
