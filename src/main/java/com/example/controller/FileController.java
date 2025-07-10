package com.example.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.example.common.Result;
import com.example.dto.FileVO;
import org.apache.commons.compress.utils.Lists;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

/**
 * 文件上传和下载相关接口
 */
@RestController
@RequestMapping("/files")
public class FileController {

    // 文件存储路径
    private static final String FILE_PATH = System.getProperty("user.dir") + "/src/main/resources/static/file/";

    /**
     * 单文件上传
     *
     * @param file 上传的文件
     * @return Result<String> 返回一个唯一标识，用于后续文件访问
     */
    @PostMapping("/upload")
    public Result<String> upload(MultipartFile file) {
        // 生成唯一标识
        String flag = System.currentTimeMillis() + "";
        String fileName = file.getOriginalFilename();
        try {
            // 将文件写入指定路径
            FileUtil.writeBytes(file.getBytes(), FILE_PATH + flag + "-" + fileName);
            System.out.println(fileName + "--上传成功");
            Thread.sleep(1L); // 防止flag重复
        } catch (Exception e) {
            System.err.println(fileName + "--文件上传失败");
        }
        return Result.success(flag);
    }

    /**
     * 多文件上传
     *
     * @param files 上传的文件数组
     * @return Result<List<FileVO>> 返回包含每个文件URL和名称的对象列表
     */
    @PostMapping("/upload/multiple")
    public Result<List<FileVO>> multipleUpload(MultipartFile[] files) {
        String filePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<FileVO> fileVOS = Lists.newArrayList();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            // 生成唯一标识
            String flag = System.currentTimeMillis() + "";
            String fileName = file.getOriginalFilename();
            FileVO fileVO = new FileVO();
            // 构造文件的访问URL
            fileVO.setUrl("http://localhost:9999/files/" + flag);
            fileVO.setName(file.getOriginalFilename());
            fileVOS.add(fileVO);
            try {
                FileUtil.writeBytes(file.getBytes(), filePath + flag + "-" + fileName);
                System.out.println(fileName + "--上传成功");
                Thread.sleep(1L);
            } catch (Exception e) {
                System.err.println(fileName + "--文件上传失败");
            }

        }
        return Result.success(fileVOS);
    }

    /**
     * 获取文件（下载）
     *
     * @param flag      文件上传时返回的唯一标识
     * @param response  HttpServletResponse对象，用于写回文件流
     */
    @GetMapping("/{flag}")
    public void avatarPath(@PathVariable String flag, HttpServletResponse response) {
        OutputStream os;
        String basePath = System.getProperty("user.dir") + "/src/main/resources/static/file/";
        List<String> fileNames = FileUtil.listFileNames(basePath);
        String avatar = fileNames.stream().filter(name -> name.contains(flag)).findAny().orElse("");
        try {
            if (StrUtil.isNotEmpty(avatar)) {
                // 设置响应头，提示浏览器下载文件
                response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(avatar, "UTF-8"));
                response.setContentType("application/octet-stream");
                // 读取文件字节流并写回response
                byte[] bytes = FileUtil.readBytes(FILE_PATH + avatar);
                os = response.getOutputStream();
                os.write(bytes);
                os.flush();
                os.close();
            }
        } catch (Exception e) {
            System.out.println("文件下载失败");
        }
    }

}
