package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Role;
import com.example.mapper.RoleMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 角色服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Role 实体类的基本 CRUD 操作。
 * </p>
 */
@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {

    @Resource
    private RoleMapper roleMapper;

}
