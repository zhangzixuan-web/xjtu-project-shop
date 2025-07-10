package com.example.service;

import com.example.entity.Banner;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.BannerMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 轮播图服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Banner 实体类的基本 CRUD 操作。
 * </p>
 */
@Service
public class BannerService extends ServiceImpl<BannerMapper, Banner> {

    @Resource
    private BannerMapper bannerMapper;

}
