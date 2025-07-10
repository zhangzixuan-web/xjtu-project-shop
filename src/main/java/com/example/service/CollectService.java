package com.example.service;

import com.example.entity.Collect;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CollectMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品收藏服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Collect 实体类的基本 CRUD 操作。
 * </p>
 */
@Service
public class CollectService extends ServiceImpl<CollectMapper, Collect> {

    @Resource
    private CollectMapper collectMapper;

}
