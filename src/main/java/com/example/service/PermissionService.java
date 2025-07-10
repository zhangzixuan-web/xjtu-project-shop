package com.example.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.mapper.PermissionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 权限菜单服务类
 */
@Service
public class PermissionService extends ServiceImpl<PermissionMapper, Permission> {

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RoleService roleService;

    /**
     * 根据角色列表获取所有不重复的权限
     * @param roles 角色列表
     * @return List<Permission>
     */
    public List<Permission> getByRoles(List<Role> roles) {
        List<Permission> permissions = new ArrayList<>();
        // 遍历每个角色
        for (Role role : roles) {
            // 获取角色的完整信息（包含权限ID列表）
            Role r = roleService.getById(role.getId());
            if (CollUtil.isNotEmpty(r.getPermission())) {
                // 遍历角色的所有权限ID
                for (Object permissionId : r.getPermission()) {
                    Permission permission = getById((int) permissionId);
                    // 确保权限不重复添加（基于路径path判断）
                    if (permissions.stream().noneMatch(p -> p.getPath().equals(permission.getPath()))) {
                        permissions.add(permission);
                    }
                }
            }
        }
        return permissions;
    }

    /**
     * 删除权限，并级联删除所有角色中对该权限的分配
     * @param id 权限ID
     */
    @Transactional
    public void delete(Long id) {
        // 1. 删除权限本身
        removeById(id);
        // 2. 遍历所有角色，移除对已删除权限的引用
        List<Role> list = roleService.list();
        for (Role role : list) {
            // 创建一个新的权限ID列表，用于存放过滤后的权限
            List<Long> newP = new ArrayList<>();
            for (Object p : role.getPermission()) {
                Long pl = Long.valueOf(p + "");
                if (!id.equals(pl)) {
                    newP.add(pl);
                }
            }
            // 更新角色的权限列表
            role.setPermission(newP);
            roleService.updateById(role);
        }
    }

}
