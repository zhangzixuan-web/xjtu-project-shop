package com.example.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.service.UserService;
import com.example.entity.Goods;
import com.example.service.GoodsService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {
    @Resource
    private GoodsService goodsService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;

    // 保留这个方法，它是我们获取当前用户的关键
    public User getUser() {
        String token = request.getHeader("token");
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    // =================  以下是管理员使用的接口, 基本保持不变  =================
    @PostMapping
    public Result<?> save(@RequestBody Goods goods) {
        goods.setCreateTime(DateUtil.now());
        // 注意：这里的save方法也需要调整，以区分是管理员创建还是商家创建
        // 为简单起见，我们假设此接口仅由管理员使用
        goodsService.save(goods);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Goods goods) {
        goodsService.updateById(goods);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        goodsService.removeById(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        Goods goods = goodsService.getById(id);
        goods.setRealPrice(goods.getPrice().multiply(BigDecimal.valueOf(goods.getDiscount())));
        return Result.success(goods);
    }

    @GetMapping
    public Result<?> findAll() {
        List<Goods> list = goodsService.findAll();
        return Result.success(list);
    }

    /**
     * 推荐商品
     * @return
     */
    @GetMapping("/recommend")
    public Result<?> recommend() {
        List<Goods> list = goodsService.recommend();
        return Result.success(list);
    }

    /**
     * 推热销商品
     * @return
     */
    @GetMapping("/sales")
    public Result<?> sales() {
        List<Goods> list = goodsService.sales();
        return Result.success(list);
    }

    /**
     * 根据分类id查询商品
     * @param id
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/byCategory/{id}")
    public Result<?> findByCategory(@PathVariable Long id,
                                    @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                    @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        IPage<Goods> page = goodsService.pageByCategory(new Page<>(pageNum, pageSize), id);
        return Result.success(page);
    }

    // 原有的管理员分页查询接口
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        IPage<Goods> page = goodsService.findPage(new Page<>(pageNum, pageSize), name);
        return Result.success(page);
    }

}
