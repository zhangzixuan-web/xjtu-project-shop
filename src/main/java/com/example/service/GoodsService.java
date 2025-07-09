package com.example.service;

import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Goods;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.GoodsMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Resource
    private GoodsMapper goodsMapper;

    // 管理员和商家公用的分页查询
    public IPage<Goods> findPage(Page<Goods> page, String name, Long merchantId) {
        return goodsMapper.findPage(page, name, merchantId);
    }

    // 重载一个给管理员用的方法，保持controller调用不变
    public IPage<Goods> findPage(Page<Goods> page, String name) {
        return this.findPage(page, name, null);
    }
    
    // 修改 save 方法，直接接收 User 对象
    public boolean save(Goods entity, User currentUser) {
        // 如果是商家在操作，则设置商家ID
        if (isUserMerchant(currentUser)) {
            entity.setMerchantId(currentUser.getId());
        }
        // 如果是管理员操作，则不设置，或者根据业务需求设置一个默认值，这里我们不设置
        return super.save(entity);
    }

    // 修改 updateById 方法，直接接收 User 对象
    public boolean updateById(Goods entity, User currentUser) {
        if (isUserMerchant(currentUser)) {
            Goods dbGoods = getById(entity.getId());
            if (!dbGoods.getMerchantId().equals(currentUser.getId())) {
                throw new CustomException("401", "无权限操作");
            }
        }
        return super.updateById(entity);
    }

    // 修改 removeById 方法，直接接收 User 对象
    public boolean removeById(Serializable id, User currentUser) {
        if (isUserMerchant(currentUser)) {
            Goods dbGoods = getById(id);
            if (dbGoods != null && !dbGoods.getMerchantId().equals(currentUser.getId())) {
                throw new CustomException("401", "无权限操作");
            }
        }
        return super.removeById(id);
    }

    // 新增一个私有辅助方法，用于安全地检查角色
    private boolean isUserMerchant(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        // 使用传统的for循环和instanceof，避免泛型类型转换问题
        for (Object roleIdObj : user.getRole()) {
            if (roleIdObj instanceof Number && ((Number) roleIdObj).longValue() == 3L) {
                return true;
            }
        }
        return false;
    }

    public IPage<Goods> pageByCategory(Page<Goods> page, Long id) {
        return goodsMapper.pageByCategory(page, id);
    }

    public Map<String, Object> getMerchantStats(Long merchantId) {
        Integer totalSales = goodsMapper.countBySales(merchantId);
        Double totalRevenue = goodsMapper.countByPrice(merchantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales != null ? totalSales : 0);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        return stats;
    }

    public List<Map<String, Object>> getSalesByCategory(Long merchantId) {
        return goodsMapper.countSalesByCategory(merchantId);
    }

    public List<Goods> recommend() {
        return goodsMapper.getRecommend();
    }

    public List<Goods> sales() {
        return goodsMapper.sales();
    }

    public List<Goods> findAll() {
        return goodsMapper.findAll();
    }
}
