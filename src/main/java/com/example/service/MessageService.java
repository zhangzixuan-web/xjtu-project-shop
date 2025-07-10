package com.example.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Message;
import com.example.entity.User;
import com.example.mapper.MessageMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 留言板或消息服务类
 */
@Service
public class MessageService extends ServiceImpl<MessageMapper, Message> {

    @Resource
    private MessageMapper messageMapper;

    @Resource
    private UserService userService;

    /**
     * 根据外键ID查询消息列表，并处理父子关系。
     * @param foreignId 关联的外键ID (例如，文章ID、商品ID等)
     * @return 经过处理的消息列表
     */
    public List<Message> findByForeign(Long foreignId) {
        // 1. 根据 foreignId 查询所有相关的消息
        LambdaQueryWrapper<Message> queryWrapper = Wrappers.<Message>lambdaQuery().eq(Message::getForeignId, foreignId).orderByDesc(Message::getId);
        List<Message> list = list(queryWrapper);
        for (Message Message : list) {
            User one = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, Message.getUsername()));
            Message.setAvatar("http://localhost:9999/files/" + one.getAvatar());
            Long parentId = Message.getParentId();
            list.stream().filter(c -> c.getId().equals(parentId)).findFirst().ifPresent(Message::setParentMessage);
        }
        return list;
    }

}
