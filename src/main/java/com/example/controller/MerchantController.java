package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.dto.GoodsVO;
import com.example.entity.Goods;
import com.example.entity.User;
import com.example.service.GoodsService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 商家相关接口
 */
@RestController
@RequestMapping("/api/merchant")
public class MerchantController {

    @Resource
    private GoodsService goodsService;
    @Resource
    private HttpServletRequest request;
    @Resource
    private UserService userService;

    /**
     * 获取商家统计数据
     * @return Result
     */
    @GetMapping("/stats")
    public Result<?> getStats() {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        Map<String, Object> stats = goodsService.getMerchantStats(currentUser.getId());
        return Result.success(stats);
    }

    /**
     * 获取按分类统计的销售数据
     * @return Result
     */
    @GetMapping("/stats/category")
    public Result<?> getStatsByCategory() {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        List<Map<String, Object>> categoryStats = goodsService.getSalesByCategory(currentUser.getId());
        return Result.success(categoryStats);
    }

    /**
     * 从token中获取当前登录用户信息
     * @return User
     */
    public User getUser() {
        String token = request.getHeader("token");
        if (token == null) return null;
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

    /**
     * 商家新增商品
     * @param goods 商品信息
     * @return Result
     */
    @PostMapping("/goods")
    public Result<?> saveForMerchant(@RequestBody Goods goods) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goods.setCreateTime(DateUtil.now());
        goodsService.save(goods, currentUser);
        return Result.success();
    }

    /**
     * 商家更新商品
     * @param goods 商品信息
     * @return Result
     */
    @PutMapping("/goods")
    public Result<?> updateForMerchant(@RequestBody Goods goods) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goodsService.updateById(goods, currentUser);
        return Result.success();
    }

    /**
     * 商家删除商品
     * @param id 商品ID
     * @return Result
     */
    @DeleteMapping("/goods/{id}")
    public Result<?> deleteForMerchant(@PathVariable Long id) {
        User currentUser = getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        goodsService.removeById(id, currentUser);
        return Result.success();
    }

    /**
     * 分页查询商家的商品
     * @param name 商品名称（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/goods/page")
    public Result<?> findMerchantPage(@RequestParam(required = false, defaultValue = "") String name,
                                      @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User currentUser = this.getUser();
        if (currentUser == null) {
            return Result.error("-1", "请先登录");
        }
        // 【修改点】: 调用新的 Service 方法 findPageForVO
        // 返回的类型自动变成了 IPage<GoodsVO>
        IPage<GoodsVO> page = goodsService.findPageForVO(new Page<>(pageNum, pageSize), name, currentUser.getId());

        // 无需其他修改，直接返回。Spring Boot 会将 IPage<GoodsVO> 正确序列化
        return Result.success(page);
    }
} 