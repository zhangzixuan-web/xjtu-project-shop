package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.mapper.CommentMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService extends ServiceImpl<CommentMapper, Comment> {

    @Resource
    private UserService userService;

    public IPage<Comment> findPage(Page<Comment> page, String name, Long merchantId) {
        return baseMapper.findPage(page, name, merchantId);
    }

    public List<Comment> findByGoodsId(Long goodsId) {
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
        query.eq(Comment::getGoodsId, goodsId);
        query.orderByDesc(Comment::getId);
        List<Comment> list = this.list(query);

        for (Comment comment : list) {
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
                comment.setAvatar(user.getAvatar());
            }
            if (comment.getParentId() != null) {
                User replyToUser = userService.getById(comment.getReplyTo());
                if (replyToUser != null) {
                    comment.setReplyToUser(replyToUser.getUsername());
                }
            }
        }

        // 筛选出所有根评论（parentId为null），然后为每个根评论设置其子评论列表
        return list.stream().filter(comment -> comment.getParentId() == null).peek(comment -> {
            comment.setReplies(list.stream()
                    .filter(c -> comment.getId().equals(c.getParentId()))
                    .collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }
} 