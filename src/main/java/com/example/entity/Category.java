package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 商品分类实体类
 * <p>
 * 对应数据库中的 `category` 表。
 * </p>
 */
@Data
@TableName("category")
public class Category extends Model<Category> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 分类名称
      */
    private String name;

    /**
      * 分类编号
      */
    private String no;

}