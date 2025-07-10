package com.example.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 商品表的数据库操作接口
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    /**
     * 自定义分页查询，可按商品名称和商家ID进行筛选。
     * @param page 分页对象
     * @param name 商品名称
     * @param merchantId 商家ID
     * @return 分页后的商品列表
     */
    IPage<Goods> findPage(Page<Goods> page, @Param("name") String name, @Param("merchantId") Long merchantId);

    /**
     * 根据分类ID分页查询商品。
     * @param page 分页对象
     * @param id 分类ID
     * @return 分页后的商品列表
     */
    @Select("select * from goods where category_id = #{id}")
    IPage<Goods> pageByCategory(Page<Goods> page, @Param("id") Long id);

    /**
     * 统计指定商家的总销量。
     * @param merchantId 商家ID
     * @return 总销量
     */
    @Select("select sum(sales) from goods where merchant_id = #{merchantId}")
    Integer countBySales(@Param("merchantId") Long merchantId);

    /**
     * 统计指定商家的总销售额。
     * @param merchantId 商家ID
     * @return 总销售额
     */
    @Select("select sum(price * sales) from goods where merchant_id = #{merchantId}")
    Double countByPrice(@Param("merchantId") Long merchantId);

    /**
     * 按分类统计指定商家的销售额。
     * @param merchantId 商家ID
     * @return 每个分类的名称和销售额列表
     */
    @Select("select c.name, sum(g.sales) as value from goods g left join category c on g.category_id = c.id where g.merchant_id = #{merchantId} group by g.category_id")
    List<Map<String, Object>> countSalesByCategory(@Param("merchantId") Long merchantId);

    /**
     * 获取推荐商品列表（具体逻辑在XML中定义）。
     * @return 推荐商品列表
     */
    List<Goods> getRecommend();

    /**
     * 按销量降序查询商品列表。
     * @return 商品列表
     */
    @Select("select * from goods order by sales desc")
    List<Goods> sales();

    /**
     * 查询所有商品（具体逻辑在XML中定义）。
     * @return 所有商品列表
     */
    List<Goods> findAll();

}
