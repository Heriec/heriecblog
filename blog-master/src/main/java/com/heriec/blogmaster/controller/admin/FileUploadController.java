package com.heriec.blogmaster.controller.admin;

import cn.hutool.core.lang.UUID;
import com.heriec.blogmaster.common.Result;
import com.heriec.blogmaster.utils.QiniuUtils;
import com.heriec.blogmaster.utils.UploadUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;

@RestController
@Api("文件上传控制器")
@Slf4j
@RequestMapping("/admin/upload")
public class FileUploadController {

    @RequiresAuthentication
    @PostMapping("/uploadImg")
    public Result insert(@RequestPart("image") MultipartFile file){

        if (file.isEmpty()) {
            return Result.fail("文件不能为空");
        }
        try {
            byte[] bytes = file.getBytes();
            String imageName = UUID.randomUUID().toString();
            try {
                //使用base64方式上传到七牛云
                String url = QiniuUtils.put64image(bytes, imageName);
                log.info("上传地址为----：" + url);
                return Result.success("上传成功！",url);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return Result.fail("上传图片异常");
        }
        return Result.fail("上传图片异常");
    }
}
