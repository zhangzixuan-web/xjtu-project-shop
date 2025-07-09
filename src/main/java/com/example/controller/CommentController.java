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

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;
    @Resource
    private UserService userService;
    @Resource
    private HttpServletRequest request;

    public User getUser() {
        String token = request.getHeader("token");
        if (token == null) return null;
        String username = JWT.decode(token).getAudience().get(0);
        return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
    }

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

    @GetMapping("/tree/{goodsId}")
    public Result<?> findTree(@PathVariable Long goodsId) {
        List<Comment> list = commentService.findByGoodsId(goodsId);
        return Result.success(list);
    }
} 