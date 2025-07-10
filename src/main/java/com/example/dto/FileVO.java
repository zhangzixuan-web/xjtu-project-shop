package com.example.dto;

import lombok.Data;

/**
 * 文件视图对象 (View Object)
 * <p>
 * 用于在文件上传后，向前端返回文件的URL和原始名称。
 * </p>
 */
@Data
public class FileVO {
    /** 文件访问URL */
    private String url;
    /** 文件原始名称 */
    private String name;
}
