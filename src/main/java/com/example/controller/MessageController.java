package com.example.controller;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.service.MessageService;
import com.example.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 留言板或消息相关接口
 */
@RestController
@RequestMapping("/api/message")
public class MessageController {
    @Resource
    private MessageService messageService;
    @Resource
    private UserService userService;
    @Resource
    HttpServletRequest request;

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
     * 新增消息
     * @param message 消息内容
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Message message) {
        // 设置消息发送者和发送时间
        message.setUsername(getUser().getUsername());
        message.setTime(DateUtil.formatDateTime(new Date()));
        return Result.success(messageService.save(message));
    }

    /**
     * 更新消息
     * @param message 消息内容
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Message message) {
        return Result.success(messageService.updateById(message));
    }

    /**
     * 删除消息
     * @param id 消息ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        messageService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询消息
     * @param id 消息ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<?> findById(@PathVariable Long id) {
        return Result.success(messageService.getById(id));
    }

    /**
     * 查询所有消息
     * @return Result
     */
    @GetMapping
    public Result<?> findAll() {
        return Result.success(messageService.list());
    }

    /**
     * 根据关联ID查询消息（例如，查询某篇文章下的所有评论）
     * @param foreignId 关联ID
     * @return Result
     */
    @GetMapping("/foreign/{foreignId}")
    public Result<?> findByForeign(@PathVariable Long foreignId) {
        return Result.success(messageService.findByForeign(foreignId));
    }

    /**
     * 分页查询消息
     * @param name 消息内容（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<?> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                                @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<Message> query = Wrappers.<Message>lambdaQuery().like(Message::getContent, name).orderByDesc(Message::getId);
        return Result.success(messageService.page(new Page<>(pageNum, pageSize), query));
    }

}
