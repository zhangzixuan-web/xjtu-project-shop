package com.example.service;

import com.example.entity.Category;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.CategoryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 商品分类服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Category 实体类的基本 CRUD 操作。
 * </p>
 */
@Service
public class CategoryService extends ServiceImpl<CategoryMapper, Category> {

    @Resource
    private CategoryMapper categoryMapper;

}
