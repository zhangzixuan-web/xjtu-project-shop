package com.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.common.handler.ListHandler;
import lombok.Data;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.type.JdbcType;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品实体类
 * <p>
 * 对应数据库中的 `goods` 表。
 * </p>
 */
@Data
@TableName(value = "goods", autoResultMap = true)
public class Goods extends Model<Goods> {
    /**
      * 主键
      */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
      * 商品名称
      */
    private String name;

    /**
      * 商品描述
      */
    private String description;

    /**
      * 商品编号
      */
    private String no;

    /**
      * 原价
      */
    private BigDecimal price;

    /**
     * 实际售价 (原价 * 折扣)
     * <p>
     * 非数据库字段，在业务逻辑中计算。
     * </p>
     */
    @TableField(exist = false)
    private BigDecimal realPrice;

    /**
      * 折扣 (例如 0.8 表示八折)
      */
    private Double discount;

    /**
      * 库存
      */
    private Integer store;

    /**
      * 点赞数
      */
    private Integer praise;

    /**
      * 销量
      */
    private Integer sales;

    /**
      * 分类ID
      */
    private Long categoryId;

    /**
     * 分类名称 (非数据库字段)
     */
    @TableField(exist = false)
    private String categoryName;

    /**
      * 商品图片 (通常是图片URL)
      */
    private String imgs;

    /**
      * 创建时间
      */
    private String createTime;

    /**
     * 是否推荐商品
     */
    private Boolean recommend;


    private Long merchantId;  // <<< 新增字段

}