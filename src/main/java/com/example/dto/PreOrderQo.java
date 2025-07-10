package com.example.dto;

import lombok.Data;

/**
 * 预订单查询对象 (Query Object)
 * <p>
 * 用于从前端接收创建预订单所需的商品列表（以JSON字符串形式）。
 * </p>
 */
@Data
public class PreOrderQo {
    /** 包含多个购物车项目信息的JSON字符串 */
    private String carts;
}
