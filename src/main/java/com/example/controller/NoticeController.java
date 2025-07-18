package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.example.common.Result;
import com.example.service.UserService;
import com.example.entity.Notice;
import com.example.service.NoticeService;
import com.example.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 公告相关接口
 */
@RestController
@RequestMapping("/api/notice")
public class NoticeController {
    @Resource
    private NoticeService noticeService;
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
     * 新增公告
     * @param notice 公告信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Notice notice) {
        notice.setTime(DateUtil.formatDateTime(new Date()));
        return Result.success(noticeService.save(notice));
    }

    /**
     * 更新公告
     * @param notice 公告信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Notice notice) {
        return Result.success(noticeService.updateById(notice));
    }

    /**
     * 删除公告
     * @param id 公告ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        noticeService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询公告
     * @param id 公告ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    /**
     * 查询所有公告
     * @return Result
     */
    @GetMapping
    public Result<?> findAll() {
        return Result.success(noticeService.list());
    }

    /**
     * 分页查询公告
     * @param name 公告标题（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Notice> query = Wrappers.<Notice>lambdaQuery().like(Notice::getTitle, name).orderByDesc(Notice::getId);;
        return Result.success(noticeService.page(new Page<>(pageNum, pageSize), query));
    }

}
