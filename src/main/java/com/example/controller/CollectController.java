package com.example.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.exception.CustomException;
import com.example.service.UserService;
import com.example.entity.Collect;
import com.example.service.CollectService;
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
 * 商品收藏相关接口
 */
@RestController
@RequestMapping("/api/collect")
public class CollectController {
    @Resource
    private CollectService collectService;
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
     * 新增收藏
     * @param collect 收藏信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Collect collect) {
        // 判断是否已收藏
        List<Collect> list = collectService.list(Wrappers.<Collect>lambdaQuery().eq(Collect::getGoodsId, collect.getGoodsId())
                .eq(Collect::getUserId, getUser().getId()));
        if (CollUtil.isNotEmpty(list)) {
            throw new CustomException("-1", "您已收藏该商品");
        }
        collect.setCreateTime(DateUtil.now());
        collectService.save(collect);
        return Result.success();
    }

    /**
     * 更新收藏
     * @param collect 收藏信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Collect collect) {
        collectService.updateById(collect);
        return Result.success();
    }

    /**
     * 删除收藏
     * @param id 收藏ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        collectService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询收藏
     * @param id 收藏ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(collectService.getById(id));
    }

    /**
     * 查询所有收藏（后台管理）
     * @return Result
     */
    @GetMapping
    public Result<?> findAll() {
        List<Collect> list = collectService.list();
        return Result.success(list);
    }

    /**
     * 分页查询收藏（后台管理）
     * @param name (未使用)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Collect> query = Wrappers.<Collect>lambdaQuery().orderByDesc(Collect::getId);
        IPage<Collect> page = collectService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

    /**
     * 分页查询当前用户的收藏（前台）
     * @param name (未使用)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page/front")
    public Result<?> findPageFront(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        User user = getUser();
        if (user == null) {
            return Result.success(new Page<>());
        }
        LambdaQueryWrapper<Collect> query = Wrappers.<Collect>lambdaQuery().eq(Collect::getUserId, getUser().getId()).orderByDesc(Collect::getId);
        IPage<Collect> page = collectService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

}
