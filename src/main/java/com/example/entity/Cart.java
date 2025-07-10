package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 购物车实体类
 * <p>
 * 对应数据库中的 `cart` 表。
 * </p>
 */
@Data
@TableName("cart")
public class Cart extends Model<Cart> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 商品ID
      */
    private Long goodsId;

    /**
      * 用户ID
      */
    private Long userId;

    /**
      * 商品数量
      */
    private Integer count;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 关联的商品对象
     * <p>
     * `@TableField(exist = false)` 表示该字段不对应数据库中的任何列。
     * 它用于在业务逻辑中临时存储和传输完整的商品信息。
     * </p>
     */
    @TableField(exist = false)
    private Goods goods;

}