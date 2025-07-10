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

/**
 * 评论服务类
 */
@Service
public class CommentService extends ServiceImpl<CommentMapper, Comment> {

    @Resource
    private UserService userService;

    /**
     * 分页查询商家的商品评论
     * @param page 分页对象
     * @param name (未使用)
     * @param merchantId 商家ID
     * @return IPage<Comment>
     */
    public IPage<Comment> findPage(Page<Comment> page, String name, Long merchantId) {
        // 调用mapper层自定义的分页查询方法
        return baseMapper.findPage(page, name, merchantId);
    }

    /**
     * 根据商品ID查询所有评论，并构造成树形结构
     * @param goodsId 商品ID
     * @return List<Comment> 根评论列表（每个根评论包含其回复列表）
     */
    public List<Comment> findByGoodsId(Long goodsId) {
        // 1. 查询出该商品的所有评论
        LambdaQueryWrapper<Comment> query = new LambdaQueryWrapper<>();
        query.eq(Comment::getGoodsId, goodsId);
        query.orderByDesc(Comment::getId);
        List<Comment> list = this.list(query);

        // 2. 遍历所有评论，填充用户信息
        for (Comment comment : list) {
            User user = userService.getById(comment.getUserId());
            if (user != null) {
                comment.setUsername(user.getUsername());
                comment.setAvatar(user.getAvatar());
            }
            // 如果是回复，则填充被回复人的用户名
            if (comment.getParentId() != null) {
                User replyToUser = userService.getById(comment.getReplyTo());
                if (replyToUser != null) {
                    comment.setReplyToUser(replyToUser.getUsername());
                }
            }
        }

        // 3. 筛选出所有根评论（parentId为null），然后为每个根评论设置其子评论列表
        return list.stream().filter(comment -> comment.getParentId() == null).peek(comment -> {
            // 在所有评论中找到当前根评论的子评论
            comment.setReplies(list.stream()
                    .filter(c -> comment.getId().equals(c.getParentId()))
                    .collect(Collectors.toList()));
        }).collect(Collectors.toList());
    }
} 