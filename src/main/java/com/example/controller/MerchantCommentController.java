package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.service.CommentService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 商家评论相关接口
 */
@RestController
@RequestMapping("/api/merchant/comment")
public class MerchantCommentController {

    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private HttpServletRequest request;

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
     * 分页查询当前商家的商品收到的评论
     * @param name (未使用)
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
        // 调用service层方法，查询与当前商家相关的评论
        IPage<Comment> page = commentService.findPage(new Page<>(pageNum, pageSize), name, currentUser.getId());
        return Result.success(page);
    }

    /**
     * 商家回复评论
     * @param comment 回复的评论内容
     * @return Result
     */
    @PostMapping
    public Result<?> reply(@RequestBody Comment comment) {
        User user = getUser();
        if (user == null) {
            return Result.error("-1", "请登录");
        }
        comment.setUserId(user.getId());
        comment.setTime(DateUtil.now());
        commentService.save(comment);
        return Result.success();
    }
} 