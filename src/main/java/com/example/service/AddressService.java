package com.example.service;

import com.example.entity.Address;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.AddressMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 地址服务类
 * <p>
 * 该服务类继承自 Mybatis-Plus 的 ServiceImpl，
 * 提供了对 Address 实体类的基本 CRUD (创建, 读取, 更新, 删除) 操作。
 * 无需编写额外方法即可实现对地址数据的增删改查。
 * </p>
 */
@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    @Resource
    private AddressMapper addressMapper;

}
