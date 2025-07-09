package com.example.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Goods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface GoodsMapper extends BaseMapper<Goods> {

    IPage<Goods> findPage(Page<Goods> page, @Param("name") String name, @Param("merchantId") Long merchantId);

    @Select("select * from goods where category_id = #{id}")
    IPage<Goods> pageByCategory(Page<Goods> page, @Param("id") Long id);

    @Select("select sum(sales) from goods where merchant_id = #{merchantId}")
    Integer countBySales(@Param("merchantId") Long merchantId);

    @Select("select sum(price * sales) from goods where merchant_id = #{merchantId}")
    Double countByPrice(@Param("merchantId") Long merchantId);

    @Select("select c.name, sum(g.sales) as value from goods g left join category c on g.category_id = c.id where g.merchant_id = #{merchantId} group by g.category_id")
    List<Map<String, Object>> countSalesByCategory(@Param("merchantId") Long merchantId);

    List<Goods> getRecommend();

    @Select("select * from goods order by sales desc")
    List<Goods> sales();

    List<Goods> findAll();

}
