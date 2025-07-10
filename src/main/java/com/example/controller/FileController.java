package com.example.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil; // 建议使用更可靠的唯一ID生成方式
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;


@RestController
public class FileController {

    @Value("${server.port:9999}")
    private String port;

    @Value("${file.ip:localhost}")
    private String ip;

    // 【修改点】: 明确定义两个不同的路径
    private static final String NEW_FILE_PATH = System.getProperty("user.dir") + "/files/";
    private static final String OLD_FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/file/";

    /**
     * 新的上传接口
     */
    @PostMapping("/api/files/upload")
    public Result<String> upload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String flag = IdUtil.fastSimpleUUID();
        String newFileName = flag + "_" + originalFilename;

        // 【修改点】: 确保文件被存放在新的路径下
        File uploadFile = new File(NEW_FILE_PATH + newFileName);

        try {
            if (!uploadFile.getParentFile().exists()) {
                uploadFile.getParentFile().mkdirs();
            }
            file.transferTo(uploadFile);
        } catch (Exception e) {
            return Result.error("-1", "文件上传失败: " + e.getMessage());
        }

        // 返回新格式的 URL
        String url = "http://" + ip + ":" + port + "/api/files/download/" + newFileName;
        return Result.success(url);
    }

    /**
     * 新的下载/预览接口 (这个接口现在其实可以被 WebConfig 的静态映射替代，但保留也无妨)
     */
    @GetMapping("/api/files/download/{fileName}")
    public void download(@PathVariable String fileName, HttpServletResponse response) {
        try {
            // 【修改点】: 从新的路径读取文件
            byte[] bytes = FileUtil.readBytes(NEW_FILE_PATH + fileName);
            response.setContentType("image/jpeg");
            OutputStream os = response.getOutputStream();
            os.write(bytes);
            os.flush();
            os.close();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * 【兼容旧URL的接口】
     */
    @GetMapping("/files/{flag}")
    public void avatarPath(@PathVariable String flag, HttpServletResponse response) {
        // 【修改点】: 从旧的路径查找文件
        List<String> fileNames = FileUtil.listFileNames(OLD_FILE_PATH);
        String avatar = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(avatar)) {
                response.setContentType("image/jpeg");
                byte[] bytes = FileUtil.readBytes(OLD_FILE_PATH + avatar);
                OutputStream os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            System.out.println("旧文件下载失败");
        }
    }
}