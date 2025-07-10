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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import com.example.dto.GoodsVO;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品服务类
 */
@Service
public class GoodsService extends ServiceImpl<GoodsMapper, Goods> {

    @Resource
    private GoodsMapper goodsMapper;
    @Resource
    private ObjectMapper objectMapper;
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

    /**
     * 【新增方法】：查询分页数据并转换为 VO
     * 这个方法将是 Controller 的新调用目标
     */
    public IPage<GoodsVO> findPageForVO(Page<Goods> pageRequest, String name, Long merchantId) {
        // 1. 调用原有的方法，从数据库获取原始数据
        // 现在 pageRequest 的类型是 Page<Goods>，与 findPage 方法的参数类型完全匹配
        IPage<Goods> goodsPage = this.findPage(pageRequest, name, merchantId);

        // 2. 将 List<Goods> 转换为 List<GoodsVO>
        List<GoodsVO> voList = goodsPage.getRecords().stream()
                .map(this::convertToVO) // 对每个 Goods 对象调用转换方法
                .collect(Collectors.toList());

        // 3. 创建一个新的分页对象 (IPage<GoodsVO>) 用于返回
        IPage<GoodsVO> resultPage = new Page<>(goodsPage.getCurrent(), goodsPage.getSize(), goodsPage.getTotal());
        resultPage.setRecords(voList);

        return resultPage;
    }

    /**
     * 【新增的私有辅助方法】：将单个 Goods 实体转换为 GoodsVO
     */
    /**
     * 【最终调试版本】：将单个 Goods 实体转换为 GoodsVO (手动赋值)
     */
    private GoodsVO convertToVO(Goods goods) {
        GoodsVO vo = new GoodsVO();

        // --- 核心修改：弃用 BeanUtils，改为手动赋值 ---
        // 我们来明确地、一步步地把数据从 Goods 实体“搬”到 GoodsVO

        vo.setId(goods.getId()); // 这是最关键的一步！
        vo.setName(goods.getName());
        vo.setDescription(goods.getDescription());
        vo.setNo(goods.getNo());
        vo.setPrice(goods.getPrice());
        vo.setDiscount(goods.getDiscount());
        vo.setStore(goods.getStore());
        vo.setPraise(goods.getPraise());
        vo.setSales(goods.getSales());
        vo.setCategoryId(goods.getCategoryId());
        // 注意：categoryName 是从 JOIN 查询来的，它在 Goods 实体里也应该有对应的字段和 get/set 方法
        vo.setCategoryName(goods.getCategoryName());
        vo.setCreateTime(goods.getCreateTime());
        vo.setRecommend(goods.getRecommend());
        vo.setMerchantId(goods.getMerchantId());

        // 处理 imgs 字段的逻辑保持不变
        String imgsJsonString = goods.getImgs();
        if (imgsJsonString != null && !imgsJsonString.isEmpty() && imgsJsonString.startsWith("[")) {
            try {
                List<String> imgList = objectMapper.readValue(imgsJsonString, new TypeReference<List<String>>() {});
                vo.setImgs(imgList);
            } catch (Exception e) {
                vo.setImgs(Collections.emptyList());
            }
        } else {
            vo.setImgs(Collections.emptyList());
        }

        // 检查 realPrice，如果 GoodsVO 中有这个字段，我们给它一个默认值
        // 假设 GoodsVO 中有 realPrice 字段
        if (vo.getRealPrice() == null) {
            vo.setRealPrice(null); // 或者 vo.setRealPrice(BigDecimal.ZERO);
        }

        return vo;
    }
}
