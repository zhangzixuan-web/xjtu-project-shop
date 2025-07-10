package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 评论实体类
 * <p>
 * 对应数据库中的 `t_comment` 表。
 * 支持父子层级的评论结构。
 * </p>
 */
@Data
@TableName("t_comment")
public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 评论内容 */
    private String content;
    /** 评论人ID */
    private Long userId;
    /** 评论时间 */
    private String time;
    /** 父评论ID，用于实现回复功能 */
    private Long parentId;
    /** 关联的商品ID */
    private Long goodsId;
    /** 回复的用户ID */
    private Long replyTo;

    /** 评论人用户名 (非数据库字段) */
    @TableField(exist = false)
    private String username;
    /** 评论人头像 (非数据库字段) */
    @TableField(exist = false)
    private String avatar;
    /** 被回复人用户名 (非数据库字段) */
    @TableField(exist = false)
    private String replyToUser;
    /** 子评论列表 (非数据库字段) */
    @TableField(exist = false)
    private List<Comment> replies;
}