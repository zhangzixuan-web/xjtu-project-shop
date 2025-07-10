package com.example.mapper;

import com.example.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * 订单表的数据库操作接口
 */
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 根据商家ID分页查询订单。
     * @param page       分页对象
     * @param merchantId 商家ID
     * @param name       订单号（用于搜索）
     * @return 分页后的订单列表
     */
    IPage<Order> findPageByMerchantId(Page<?> page, @Param("merchantId") Long merchantId, @Param("name") String name);

    /**
     * 检查指定订单是否属于指定商家。
     * @param orderId    订单ID
     * @param merchantId 商家ID
     * @return 如果订单属于该商家，则返回1，否则返回0。
     */
    Integer checkOrderOwner(@Param("orderId") Long orderId, @Param("merchantId") Long merchantId);
}
