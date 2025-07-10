package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.CommentService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 商品评论相关接口
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

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
     * 新增评论
     * @param comment 评论信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Comment comment) {
        User user = getUser();
        if (user == null) {
            return Result.error("-1", "请登录");
        }
        comment.setUserId(user.getId());
        comment.setTime(DateUtil.now());
        commentService.save(comment);
        return Result.success();
    }

    /**
     * 删除评论
     * @param id 评论ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        User user = getUser();
        if (user == null) {
            return Result.error("-1", "请登录");
        }
        Comment comment = commentService.getById(id);
        if (!user.getId().equals(comment.getUserId())) {
            throw new CustomException("-1", "无权删除");
        }
        commentService.removeById(id);
        return Result.success();
    }

    /**
     * 查询指定商品的所有评论（树形结构）
     * @param goodsId 商品ID
     * @return Result
     */
    @GetMapping("/tree/{goodsId}")
    public Result<?> findTree(@PathVariable Long goodsId) {
        List<Comment> list = commentService.findByGoodsId(goodsId);
        return Result.success(list);
    }
} 