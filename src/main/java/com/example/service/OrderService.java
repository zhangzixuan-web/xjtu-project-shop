package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Order;
import com.example.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderService extends ServiceImpl<OrderMapper, Order> {

    @Autowired
    private OrderMapper orderMapper; // 或 OrderRepository

    @Autowired
    private LogisticsSimulatorService logisticsSimulatorService;

    // 管理员发货操作
    public void shipOrder(Long orderId, String company, String number) {
        // 1. 查找订单
        Order order = orderMapper.selectById(orderId); // 根据你的持久层框架选择方法
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

    // 用户或管理员查询物流信息
    public String getOrderTrackingDetails(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null || order.getShippingDetails() == null) {
            return "[]"; // 返回空JSON数组表示没有信息
        }
        return order.getShippingDetails();
    }

}


