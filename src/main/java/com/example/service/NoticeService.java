package com.example.service;

import com.example.entity.Notice;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.NoticeMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 公告服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Notice 实体类的基本 CRUD 操作。
 * </p>
 */
@Service
public class NoticeService extends ServiceImpl<NoticeMapper, Notice> {

    @Resource
    private NoticeMapper noticeMapper;

}
