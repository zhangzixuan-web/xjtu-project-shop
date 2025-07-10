package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

/**
 * 权限实体类
 * <p>
 * 对应数据库中的 `t_permission` 表。
 * 通常用于定义前端路由、菜单或操作的权限。
 * </p>
 */
@Data
@TableName("t_permission")
public class Permission extends Model<Permission> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 权限名称，用于显示 (例如 "用户管理") */
    private String name;

    /** 权限标识，通常对应前端路由路径 (例如 "/user") */
    private String path;

    /** 权限描述 */
    private String description;

    /** 菜单图标 */
    private String icon;

}
