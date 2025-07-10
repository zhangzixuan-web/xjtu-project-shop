package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.service.UserService;
import com.example.entity.Banner;
import com.example.service.BannerService;
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
 * 轮播图相关接口
 */
@RestController
@RequestMapping("/api/banner")
public class BannerController {
    @Resource
    private BannerService bannerService;
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
     * 新增轮播图
     * @param banner 轮播图信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Banner banner) {
        bannerService.save(banner);
        return Result.success();
    }

    /**
     * 更新轮播图
     * @param banner 轮播图信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Banner banner) {
        bannerService.updateById(banner);
        return Result.success();
    }

    /**
     * 删除轮播图
     * @param id 轮播图ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        bannerService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询轮播图
     * @param id 轮播图ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(bannerService.getById(id));
    }

    /**
     * 查询所有轮播图
     * @return Result
     */
    @GetMapping
    public Result<?> findAll() {
        List<Banner> list = bannerService.list();
        return Result.success(list);
    }

    /**
     * 分页查询轮播图
     * @param name 轮播图URL的一部分（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Banner> query = Wrappers.<Banner>lambdaQuery().orderByDesc(Banner::getId);
        if (StrUtil.isNotBlank(name)) {
            query.like(Banner::getUrl, name);
        }
        IPage<Banner> page = bannerService.page(new Page<>(pageNum, pageSize), query);
        return Result.success(page);
    }

}
