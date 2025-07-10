package com.example.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户服务类
 */
@Service
public class UserService extends ServiceImpl<UserMapper, User> {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleService roleService;

    @Resource
    private PermissionService permissionService;

    /**
     * 用户登录
     * @param user 包含用户名和密码的用户对象
     * @return 登录成功后的用户对象，包含权限信息
     */
    public User login(User user) {
        // 根据用户名和密码查询用户
        LambdaQueryWrapper<User> queryWrapper = Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername()).eq(User::getPassword, user.getPassword());
        User one = getOne(queryWrapper);
        if (one == null) {
            throw new CustomException("-1", "账号或密码错误");
        }
        // 查询并设置用户的权限
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    /**
     * 用户注册
     * @param user 注册用户信息
     * @return 注册成功后的用户对象
     */
    public User register(User user) {
        // 检查用户名是否已存在
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
        if (one != null) {
            throw new CustomException("-1", "用户已注册");
        }
        // 如果没有提供密码，设置默认密码
        if (user.getPassword() == null) {
            user.setPassword("123456");
        }
        // 分配默认角色（ID为2，通常是普通用户）
        user.setRole(CollUtil.newArrayList(2L));
        save(user);
        return getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, user.getUsername())));
    }

    /**
     * 根据用户ID获取其所有权限
     * @param userId 用户ID
     * @return 权限列表
     */
    public List<Permission> getPermissions(Long userId) {
        User user = getById(userId);
        List<Permission> permissions = new ArrayList<>();
        List<Long> role = user.getRole();
        if (role != null) {
            // 遍历用户的所有角色
            for (Object roleId : role) {
                // 根据角色ID查询角色信息
                Role realRole = roleService.getById((int) roleId);
                // 如果角色有关联的权限
                if (CollUtil.isNotEmpty(realRole.getPermission())) {
                    // 遍历角色的所有权限ID
                    for (Object permissionId : realRole.getPermission()) {
                        Permission permission = permissionService.getById((int) permissionId);
                        // 确保权限不重复添加（基于路径path判断）
                        if (permission != null && permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                            permissions.add(permission);
                        }
                    }
                }
            }
            user.setPermission(permissions);
        }
        return permissions;
    }

    /**
     * 根据用户名查询用户，并附加权限信息
     * @param username 用户名
     * @return 用户对象
     */
    public User getbyUsername(String username) {
        User one = getOne((Wrappers.<User>lambdaQuery().eq(User::getUsername, username)));
        one.setPermission(getPermissions(one.getId()));
        return one;
    }

    /**
     * 根据ID查询用户，并附加权限信息
     * @param id 用户ID
     * @return 用户对象
     */
    public User findById(Long id) {
        User user = getById(id);
        user.setPermission(getPermissions(id));
        return user;
    }
}
