package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.OrderGoods;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单-商品关联表的数据库操作接口
 */
public interface OrderGoodsMapper extends BaseMapper<OrderGoods> {

    /**
     * 根据订单ID查询所有关联的商品记录。
     * @param orderId 订单ID
     * @return 订单-商品关联记录的列表
     */
    @Select("select * from order_goods where order_id = #{orderId}")
    List<OrderGoods> findGoodsByOrderId(@Param("orderId") Long orderId);
}
