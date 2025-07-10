package com.example.common;



import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // --- 新增的、用于处理新上传文件的映射 ---
        // 映射规则：当浏览器访问 /api/files/download/** 的URL时，
        // Spring Boot会去 "file:/你的项目绝对路径/files/" 目录下查找对应的文件
        String newFilePath = "file:" + System.getProperty("user.dir") + "/files/";
        System.out.println("新文件映射路径: " + newFilePath); // 打印出来，方便调试
        registry.addResourceHandler("/api/files/download/**")
                .addResourceLocations(newFilePath);

        // --- 保留的、用于兼容旧文件的映射 ---
        // 映射规则：当浏览器访问 /files/** 的URL时（没有/api），
        // Spring Boot会去 "classpath:/static/file/" 目录下查找对应的文件
        // "classpath:/static/" 是 Spring Boot 默认的静态资源目录，所以这里可以简化
        String oldFilePath = "classpath:/static/file/";
        System.out.println("旧文件映射路径: " + oldFilePath);
        registry.addResourceHandler("/files/**")
                .addResourceLocations(oldFilePath);
    }
}