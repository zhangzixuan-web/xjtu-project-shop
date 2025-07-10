package com.example.controller;

import cn.hutool.json.JSONUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Cart;
import com.example.entity.Order;
import com.example.entity.User;
import com.example.service.OrderGoodsService;
import com.example.service.OrderService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 商家订单相关接口
 */
@RestController
@RequestMapping("/api/merchant/order")
public class MerchantOrderController {

    @Resource
    private OrderService orderService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;
    @Resource
    private OrderGoodsService orderGoodsService;

    /**
     * 从token中获取当前登录用户信息
     * @return User
     */
    public User getUser() {
        String token = request.getHeader("token");
        if (token == null) {
            return null;
        }
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    /**
     * 商家发货
     * @param id 订单ID
     * @param params 包含物流公司和单号的map
     * @return Result
     */
    @PostMapping("/{id}/ship")
    public Result<?> ship(@PathVariable Long id, @RequestBody Map<String, String> params) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        String company = params.get("company");
        String number = params.get("number");
        orderService.shipOrderByMerchant(id, company, number, currentUser);
        return Result.success();
    }

    /**
     * 分页查询商家的订单
     * @param name 订单号的一部分（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        Long merchantId = currentUser.getId();
        // 分页查询当前商家的订单
        IPage<Order> page = orderService.findPageByMerchantId(new Page<>(pageNum, pageSize), merchantId, name);

        // 遍历订单，为每个订单填充商品信息
        for (Order order : page.getRecords()) {
            Long orderId = order.getId();
            List<Cart> carts = orderGoodsService.findByOrderId(orderId);
            order.setCarts(JSONUtil.toJsonStr(carts));
        }
        return Result.success(page);
    }
} 