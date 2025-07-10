package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.service.UserService;
import com.example.entity.OrderGoods;
import com.example.service.OrderGoodsService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 订单-商品关联信息相关接口
 */
@RestController
@RequestMapping("/api/orderGoods")
public class OrderGoodsController {
    @Resource
    private OrderGoodsService orderGoodsService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;

    /**
     * 从token中获取当前登录用户信息
     * @return User
     */
    public User getUser() {
        String token = request.getHeader("token");
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    /**
     * 新增订单-商品关联记录
     * @param orderGoods 关联信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody OrderGoods orderGoods) {
        orderGoodsService.save(orderGoods);
        return Result.success();
    }

    /**
     * 更新订单-商品关联记录
     * @param orderGoods 关联信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody OrderGoods orderGoods) {
        orderGoodsService.updateById(orderGoods);
        return Result.success();
    }

    /**
     * 删除订单-商品关联记录
     * @param id 记录ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        orderGoodsService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询订单-商品关联记录
     * @param id 记录ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(orderGoodsService.getById(id));
    }

    /**
     * 查询所有订单-商品关联记录
     * @return Result
     */
    @GetMapping
    public Result<?> findAll() {
        List<OrderGoods> list = orderGoodsService.list();
        return Result.success(list);
    }

    /**
     * 分页查询订单-商品关联记录
     * @param name (未使用)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<OrderGoods> query = Wrappers.<OrderGoods>lambdaQuery().orderByDesc(OrderGoods::getId);
        IPage<OrderGoods> page = orderGoodsService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

}
