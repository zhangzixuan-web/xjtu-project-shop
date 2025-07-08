package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Goods;
import com.example.entity.User;
import com.example.service.GoodsService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/merchant/goods")
public class MerchantController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;

    public User getUser() {
        String token = request.getHeader("token");
        if (token == null) return null;
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    @PostMapping
    public Result<?> saveForMerchant(@RequestBody Goods goods) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goods.setCreateTime(DateUtil.now());
        goodsService.save(goods, currentUser);
        return Result.success();
    }

    @PutMapping
    public Result<?> updateForMerchant(@RequestBody Goods goods) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goodsService.updateById(goods, currentUser);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteForMerchant(@PathVariable Long id) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goodsService.removeById(id, currentUser);
        return Result.success();
    }

    @GetMapping("/page")
    public Result<?> findMerchantPage(@RequestParam(required = false, defaultValue = "") String name,
                                      @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User currentUser = this.getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        IPage<Goods> page = goodsService.findPage(new Page<>(pageNum, pageSize), name, currentUser.getId());
        return Result.success(page);
    }
} 