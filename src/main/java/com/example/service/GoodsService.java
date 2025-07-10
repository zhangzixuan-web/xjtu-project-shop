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

/**
 * 商品服务类
 */
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Resource
    private GoodsMapper goodsMapper;

    /**
     * 分页查询商品（管理员和商家公用）
     * @param page 分页对象
     * @param name 商品名称（用于搜索）
     * @param merchantId 商家ID（如果为null，则查询所有商家的商品）
     * @return IPage<Goods>
     */
    public IPage<Goods> findPage(Page<Goods> page, String name, Long merchantId) {
        return goodsMapper.findPage(page, name, merchantId);
    }

    /**
     * 分页查询商品（管理员专用，重载方法）
     * @param page 分页对象
     * @param name 商品名称（用于搜索）
     * @return IPage<Goods>
     */
    public IPage<Goods> findPage(Page<Goods> page, String name) {
        return this.findPage(page, name, null);
    }

    /**
     * 保存商品信息，区分商家和管理员
     * @param entity 商品实体
     * @param currentUser 当前登录用户
     * @return boolean
     */
    public boolean save(Goods entity, User currentUser) {
        // 如果当前用户是商家，则将商品与该商家关联
        if (isUserMerchant(currentUser)) {
            entity.setMerchantId(currentUser.getId());
        }
        return super.save(entity);
    }

    /**
     * 更新商品信息，包含商家权限校验
     * @param entity 商品实体
     * @param currentUser 当前登录用户
     * @return boolean
     */
    public boolean updateById(Goods entity, User currentUser) {
        // 如果当前用户是商家，校验其是否有权限修改该商品
        if (isUserMerchant(currentUser)) {
            Goods dbGoods = getById(entity.getId());
            if (!dbGoods.getMerchantId().equals(currentUser.getId())) {
                throw new CustomException("401", "无权限操作");
            }
        }
        return super.updateById(entity);
    }

    /**
     * 删除商品信息，包含商家权限校验
     * @param id 商品ID
     * @param currentUser 当前登录用户
     * @return boolean
     */
    public boolean removeById(Serializable id, User currentUser) {
        // 如果当前用户是商家，校验其是否有权限删除该商品
        if (isUserMerchant(currentUser)) {
            Goods dbGoods = getById(id);
            if (dbGoods != null && !dbGoods.getMerchantId().equals(currentUser.getId())) {
                throw new CustomException("401", "无权限操作");
            }
        }
        return super.removeById(id);
    }

    /**
     * 检查用户是否为商家
     * @param user 用户对象
     * @return boolean
     */
    private boolean isUserMerchant(User user) {
        if (user == null || user.getRole() == null) {
            return false;
        }
        // "商家"角色ID通常为3
        // 注意：这里的角色判断逻辑依赖于前端传来的角色ID列表
        for (Object roleIdObj : user.getRole()) {
            if (roleIdObj instanceof Number && ((Number) roleIdObj).longValue() == 3L) {
                return true;
            }
        }
        return false;
    }

    /**
     * 根据分类ID分页查询商品
     * @param page 分页对象
     * @param id 分类ID
     * @return IPage<Goods>
     */
    public IPage<Goods> pageByCategory(Page<Goods> page, Long id) {
        return goodsMapper.pageByCategory(page, id);
    }

    /**
     * 获取商家的统计数据（总销量、总销售额）
     * @param merchantId 商家ID
     * @return Map<String, Object>
     */
    public Map<String, Object> getMerchantStats(Long merchantId) {
        Integer totalSales = goodsMapper.countBySales(merchantId);
        Double totalRevenue = goodsMapper.countByPrice(merchantId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSales", totalSales != null ? totalSales : 0);
        stats.put("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        return stats;
    }

    /**
     * 按分类获取商家的销售数据
     * @param merchantId 商家ID
     * @return List<Map<String, Object>>
     */
    public List<Map<String, Object>> getSalesByCategory(Long merchantId) {
        return goodsMapper.countSalesByCategory(merchantId);
    }

    /**
     * 获取推荐商品
     * @return List<Goods>
     */
    public List<Goods> recommend() {
        return goodsMapper.getRecommend();
    }

    /**
     * 获取热销商品
     * @return List<Goods>
     */
    public List<Goods> sales() {
        return goodsMapper.sales();
    }

    /**
     * 获取所有商品
     * @return List<Goods>
     */
    public List<Goods> findAll() {
        return goodsMapper.findAll();
    }
}
