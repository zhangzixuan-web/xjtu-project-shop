package com.example.entity;

import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;


/**
 * 订单-商品关联实体类
 * <p>
 * 对应数据库中的 `order_goods` 表，作为订单和商品的多对多关联表。
 * </p>
 */
@Data
@TableName("order_goods")
public class OrderGoods extends Model<OrderGoods> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 订单ID
      */
    private Long orderId;

    /**
      * 商品ID
      */
    private Long goodsId;

    /**
      * 商品数量
      */
    private Integer count;

}