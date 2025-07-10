package com.example.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Order;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 订单服务类
 */
@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderMapper orderMapper; // 或 OrderRepository

    @Autowired
    private LogisticsSimulatorService logisticsSimulatorService;

    /**
     * 商家发货操作
     * @param orderId   订单ID
     * @param company   物流公司
     * @param number    物流单号
     * @param merchant  当前操作的商家
     */
    public void shipOrderByMerchant(Long orderId, String company, String number, User merchant) {
        // 校验该订单是否属于当前商家
        Integer count = baseMapper.checkOrderOwner(orderId, merchant.getId());
        if (count == 0) {
            throw new CustomException("-1", "无权操作");
        }
        // 1. 查找订单并校验状态
        Order order = getById(orderId);
        if (order == null || !"待发货".equals(order.getState())) {
            throw new CustomException("-1", "订单状态不正确，无法发货");
        }

        // 2. 更新订单信息
        order.setState("已发货");
        order.setShippingCompany(company);
        order.setShippingNumber(number);

        // 3. 生成并保存模拟物流轨迹
        String trackingJson = logisticsSimulatorService.generateFakeTrackingData(company);
        order.setShippingDetails(trackingJson);

        // 4. 保存回数据库
        updateById(order);
    }

    /**
     * 管理员发货操作
     * @param orderId 订单ID
     * @param company 物流公司
     * @param number  物流单号
     */
    public void shipOrder(Long orderId, String company, String number) {
        // 1. 查找订单并校验状态
        Order order = orderMapper.selectById(orderId);
        if (order == null || !"待发货".equals(order.getState())) {
            throw new RuntimeException("订单状态不正确，无法发货");
        }

        // 2. 更新订单信息
        order.setState("已发货");
        order.setShippingCompany(company);
        order.setShippingNumber(number);

        // 3. 生成并保存模拟物流轨迹
        String trackingJson = logisticsSimulatorService.generateFakeTrackingData(company);
        order.setShippingDetails(trackingJson);

        // 4. 保存回数据库
        orderMapper.updateById(order);
    }

    /**
     * 根据商家ID分页查询订单
     * @param page       分页对象
     * @param merchantId 商家ID
     * @param name       订单号（用于搜索）
     * @return IPage<Order>
     */
    public IPage<Order> findPageByMerchantId(Page<?> page, Long merchantId, String name) {
        return baseMapper.findPageByMerchantId(page, merchantId, name);
    }

    /**
     * 获取订单的物流跟踪详情
     * @param orderId 订单ID
     * @return JSON格式的物流信息字符串
     */
    public String getOrderTrackingDetails(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getShippingDetails() == null) {
            // 返回空JSON数组表示没有信息
            return "[]";
        }
        return order.getShippingDetails();
    }

}


