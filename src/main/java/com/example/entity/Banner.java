package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 轮播图实体类
 * <p>
 * 对应数据库中的 `banner` 表。
 * </p>
 */
@Data
@TableName("banner")
public class Banner extends Model<Banner> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 图片地址
      */
    private String img;

    /**
      * 点击轮播图后跳转的URL
      */
    private String url;

}