package com.example.service;

import com.example.entity.Cart;
import com.example.entity.Goods;
import com.example.entity.OrderGoods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.OrderGoodsMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单-商品关联服务类
 */
@Service
public class OrderGoodsService extends ServiceImpl<OrderGoodsMapper, OrderGoods> {

    @Resource
    private OrderGoodsMapper orderGoodsMapper;
    @Resource
    private GoodsService goodsService;

    /**
     * 根据订单ID查询该订单关联的所有商品列表。
     * <p>
     * 该方法会查询订单-商品关联表，然后根据商品ID获取每个商品的详细信息，
     * 最终将结果组装成一个类似于购物车的对象列表返回。
     * </p>
     *
     * @param orderId 订单ID
     * @return List<Cart> 包含商品详细信息的列表
     */
    public List<Cart> findByOrderId(Long orderId) {
        List<Cart> carts = new ArrayList<>();
        // 1. 根据订单ID查询所有订单-商品关联记录
        List<OrderGoods> orderGoods = orderGoodsMapper.findGoodsByOrderId(orderId);
        // 2. 遍历关联记录
        for (OrderGoods orderGood : orderGoods) {
            Long goodsId = orderGood.getGoodsId();
            // 3. 根据商品ID获取商品详细信息
            Goods goods = goodsService.getById(goodsId);
            // 4. 将商品信息和数量组装成一个Cart对象
            Cart cart = new Cart();
            cart.setGoods(goods);
            cart.setGoodsId(goodsId);
            cart.setCount(orderGood.getCount());
            carts.add(cart);
        }
        return carts;
    }

}
