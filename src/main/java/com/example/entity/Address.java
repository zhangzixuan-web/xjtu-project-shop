package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 地址实体类
 * <p>
 * 对应数据库中的 `address` 表。
 * </p>
 */
@Data
@TableName("address")
public class Address extends Model<Address> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 联系人
      */
    private String linkUser;

    /**
      * 联系地址
      */
    private String linkAddress;

    /**
      * 联系电话
      */
    private String linkPhone;

    /**
     * 关联的用户ID
     */
    private Long userId;

}