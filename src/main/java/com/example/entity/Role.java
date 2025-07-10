package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.common.handler.ListHandler;
import lombok.Data;

import java.util.List;

/**
 * 角色实体类
 * <p>
 * 对应数据库中的 `t_role` 表。
 * </p>
 */
@Data
@TableName(value = "t_role", autoResultMap = true)
public class Role extends Model<Role> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String name;

    /** 角色描述 */
    private String description;

    /**
     * 关联的权限ID列表
     * <p>
     * 使用自定义的 {@link ListHandler} 将 List<Long> 类型与数据库中的
     * 字符串类型（如 VARCHAR 或 TEXT）进行转换。
     * </p>
     */
    @TableField(typeHandler = ListHandler.class)
    private List<Long> permission;


}
