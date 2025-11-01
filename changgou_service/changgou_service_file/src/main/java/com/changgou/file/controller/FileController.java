package com.changgou.file.controller;

import com.changgou.entity.Result;
import com.changgou.entity.StatusCode;
import com.changgou.file.util.FastDFSClient;
import com.changgou.file.util.FastDFSFile;
import com.changgou.file.util.FastDFSUtils;
import org.apache.commons.lang.StringUtils;
import org.csource.fastdfs.ClientGlobal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.Charset;

@RestController
@RequestMapping("/file")
public class FileController {

    @PostMapping("/upload")
    public Result uploadFile(MultipartFile file) {
        try {
            System.out.println("[" + ClientGlobal.g_charset + "]");
            for (char c : ClientGlobal.g_charset.toCharArray()) {
                System.out.println((int)c);
            }

            // 判断文件是否存在
            if (file == null) {
                throw new RuntimeException("文件不存在");
            }

            // 获取文件完整名称
            String originalFilename = file.getOriginalFilename();
            if (StringUtils.isEmpty(originalFilename)) {
                throw new RuntimeException("文件不存在");
            }

            // 获取文件扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);// 不需要拓展名中的点

            // 获取文件内容
            byte[] content = file.getBytes();

            // 创建文件上传的封装实体类
            FastDFSFile fastDFSFile = new FastDFSFile(originalFilename, content, extName);

            // 基于工具类进行文件上传，并接收返回参数 String[]
            String[] uploadResult = FastDFSClient.upload(fastDFSFile);

            // 封装返回结果
            String url = FastDFSClient.getTrackerUrl() + uploadResult[0] + "/" + uploadResult[1];
            return new Result(true, StatusCode.OK, "上传成功", url);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new Result(false, StatusCode.ERROR, "上传失败");
    }
}
