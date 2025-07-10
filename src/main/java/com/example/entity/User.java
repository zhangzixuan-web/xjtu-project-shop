package com.example.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.example.common.handler.ListHandler;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 用户实体类
 * <p>
 * 对应数据库中的 `t_user` 表。
 * </p>
 */
@Data
@TableName(value = "t_user", autoResultMap = true)
public class User extends Model<User> {
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;
    /** 昵称 */
    private String nickName;

    /** 密码 */
    private String password;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;
    /** 地址 */
    private String address;
    /** 年龄 */
    private String age;
    /** 账户余额 */
    private BigDecimal account;

    /**
     * 认证Token (非数据库字段)
     * <p>
     * 在用户登录后，用于临时存储生成的JWT。
     * </p>
     */
    @TableField(exist = false)
    private String token;

    /**
     * 关联的角色ID列表
     * <p>
     * 使用自定义的 {@link ListHandler} 进行数据库类型转换。
     * </p>
     */
    @TableField(typeHandler = ListHandler.class)
    private List<Long> role;

    /**
     * 用户的权限列表 (非数据库字段)
     * <p>
     * 在用户登录或查询时，由业务逻辑层根据用户的角色列表动态填充。
     * </p>
     */
    @TableField(exist = false)
    private List<Permission> permission;

}
