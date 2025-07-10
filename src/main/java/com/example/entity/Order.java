package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.math.BigDecimal;
import java.util.List;

/**
 * 订单实体类
 * <p>
 * 对应数据库中的 `t_order` 表。
 * </p>
 */
@Data
@TableName("t_order")
public class Order extends Model<Order> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 订单编号
      */
    private String orderNo;

    /**
      * 总价
      */
    private BigDecimal totalPrice;

    /**
      * 下单人ID
      */
    private Long userId;

    /**
      * 联系人
      */
    private String linkUser;

    /**
      * 联系电话
      */
    private String linkPhone;

    /**
      * 送货地址
      */
    private String linkAddress;

    /**
      * 状态 (例如：待付款、待发货、已发货、已完成)
      */
    private String state;

    /**
      * 创建时间
      */
    private String createTime;

    /**
     * 物流公司
     */
    private String shippingCompany;

    /**
     * 物流单号
     */
    private String shippingNumber;

    /**
     * 物流详情 (通常是JSON格式的跟踪信息)
     */
    private String shippingDetails;

    /**
     * 购物车商品列表 (非数据库字段)
     * <p>
     * 用于在创建订单时从前端接收商品信息的JSON字符串。
     * </p>
     */
    @TableField(exist = false)
    private String carts;

    /**
     * 订单类型 (非数据库字段)
     * <p>
     * 用于区分订单来源，例如 1表示来自购物车，0表示直接购买。
     * </p>
     */
    @TableField(exist = false)
    private Integer type;

}