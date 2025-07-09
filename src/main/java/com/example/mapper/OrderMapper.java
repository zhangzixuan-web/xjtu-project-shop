package com.example.mapper;

import com.example.entity.Order;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

public interface OrderMapper extends BaseMapper<Order> {

    IPage<Order> findPageByMerchantId(Page<?> page, @Param("merchantId") Long merchantId, @Param("name") String name);

    Integer checkOrderOwner(@Param("orderId") Long orderId, @Param("merchantId") Long merchantId);
}
