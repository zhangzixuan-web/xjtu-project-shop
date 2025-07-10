package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Permission;
import com.example.entity.Role;
import com.example.service.LogService;
import com.example.service.PermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 权限菜单相关接口
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {
    @Resource
    private PermissionService permissionService;
    @Resource
    private LogService logService;

    /**
     * 新增权限菜单
     * @param permission 权限信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Permission permission) {
        logService.log(StrUtil.format("新增权限菜单：{}", permission.getName()));
        return Result.success(permissionService.save(permission));
    }

    /**
     * 更新权限菜单
     * @param permission 权限信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Permission permission) {
        logService.log(StrUtil.format("更新权限菜单：{}", permission.getName()));
        return Result.success(permissionService.updateById(permission));
    }

    /**
     * 删除权限菜单
     * @param id 权限ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Permission permission = permissionService.getById(id);
        logService.log(StrUtil.format("删除权限菜单：{}", permission.getName()));
        permissionService.delete(id);
        return Result.success();
    }

    /**
     * 根据ID查询权限菜单
     * @param id 权限ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<Permission> findById(@PathVariable Long id) {
        return Result.success(permissionService.getById(id));
    }

    /**
     * 查询所有权限菜单
     * @return Result
     */
    @GetMapping
    public Result<List<Permission>> findAll() {
        return Result.success(permissionService.list());
    }

    /**
     * 分页查询权限菜单
     * @param name 权限名称（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<IPage<Permission>> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                              @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                              @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(permissionService.page(new Page<>(pageNum, pageSize), Wrappers.<Permission>lambdaQuery().like(Permission::getName, name)));
    }

    /**
     * 根据角色列表查询对应的权限菜单
     * @param roles 角色列表
     * @return Result
     */
    @PostMapping("/getByRoles")
    public Result<List<Permission>> getByRoles(@RequestBody List<Role> roles) {
        return Result.success(permissionService.getByRoles(roles));
    }

}
