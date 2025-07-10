package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 公告实体类
 * <p>
 * 对应数据库中的 `t_notice` 表。
 * </p>
 */
@Data
@TableName("t_notice")
public class Notice extends Model<Notice> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 标题
      */
    private String title;

    /**
      * 内容
      */
    private String content;

    /**
      * 发布时间
      */
    private String time;

}