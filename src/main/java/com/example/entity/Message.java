package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;


/**
 * 留言或消息实体类
 * <p>
 * 对应数据库中的 `t_message` 表。
 * </p>
 */
@Data
@TableName("t_message")
public class Message extends Model<Message> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 内容
      */
    private String content;

    /**
      * 评论人 (用户名)
      */
    private String username;

    /**
      * 评论时间
      */
    private String time;

    /**
      * 父ID，用于建立消息的层级关系（回复）
      */
    private Long parentId;

    /**
     * 父消息对象 (非数据库字段)
     * <p>
     * 用于在业务逻辑中存储和传输父消息的完整信息。
     * </p>
     */
    @TableField(exist = false)
    private Message parentMessage;

    /**
     * 关联的外部ID (例如，文章ID、帖子ID等)
     */
    private Long foreignId;

    /**
     * 评论人头像URL (非数据库字段)
     */
    @TableField(exist = false)
    private String avatar;


}
