package com.example.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.common.Result;
import com.example.entity.Role;
import com.example.service.LogService;
import com.example.service.RoleService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 角色相关接口
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {
    @Resource
    private RoleService roleService;
    @Resource
    private LogService logService;

    /**
     * 新增角色
     * @param role 角色信息
     * @return Result
     */
    @PostMapping
    public Result<?> save(@RequestBody Role role) {
        logService.log(StrUtil.format("新增角色：{}", role.getName()));
        return Result.success(roleService.save(role));
    }

    /**
     * 更新角色
     * @param role 角色信息
     * @return Result
     */
    @PutMapping
    public Result<?> update(@RequestBody Role role) {
        logService.log(StrUtil.format("更新角色：{}", role.getName()));
        return Result.success(roleService.updateById(role));
    }

    /**
     * 删除角色
     * @param id 角色ID
     * @return Result
     */
    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        Role role = roleService.getById(id);
        logService.log(StrUtil.format("删除角色：{}", role.getName()));

        roleService.removeById(id);
        return Result.success();
    }

    /**
     * 根据ID查询角色
     * @param id 角色ID
     * @return Result
     */
    @GetMapping("/{id}")
    public Result<Role> findById(@PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    /**
     * 查询所有角色
     * @return Result
     */
    @GetMapping
    public Result<List<Role>> findAll() {
        return Result.success(roleService.list());
    }

    /**
     * 分页查询角色
     * @param name 角色名称（用于搜索）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return Result
     */
    @GetMapping("/page")
    public Result<IPage<Role>> findPage(@RequestParam(required = false, defaultValue = "") String name,
                                        @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                        @RequestParam(required = false, defaultValue = "10") Integer pageSize) {
        return Result.success(roleService.page(new Page<>(pageNum, pageSize), Wrappers.<Role>lambdaQuery().like(Role::getName, name)));
    }

}
