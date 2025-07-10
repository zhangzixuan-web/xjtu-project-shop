package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 商品收藏实体类
 * <p>
 * 对应数据库中的 `collect` 表。
 * </p>
 */
@Data
@TableName("collect")
public class Collect extends Model<Collect> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 商品名称 (冗余字段，用于提高查询性能)
      */
    private String goodsName;

    /**
      * 商品图片 (冗余字段，用于提高查询性能)
      */
    private String goodsImg;

    /**
      * 商品ID
      */
    private String goodsId;

    /**
      * 用户ID
      */
    private String userId;

    /**
      * 收藏时间
      */
    private String createTime;

}