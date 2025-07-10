package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;


/**
 * 日志实体类
 * <p>
 * 对应数据库中的 `t_log` 表。
 * </p>
 */
@Data
@TableName("t_log")
public class Log extends Model<Log> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 操作内容
      */
    private String content;

    /**
      * 操作时间
      */
    private String time;

    /**
      * 操作人 (用户名)
      */
    private String user;

    /**
     * 操作人IP地址
     */
    private String ip;

}
