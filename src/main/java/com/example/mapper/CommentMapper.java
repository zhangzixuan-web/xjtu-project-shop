package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.entity.Comment;
import org.apache.ibatis.annotations.Param;

/**
 * 评论表的数据库操作接口
 */
public interface CommentMapper extends BaseMapper<Comment> {

    /**
     * 自定义分页查询，用于商家查询其商品的评论。
     * <p>
     * 该方法的具体 SQL 实现位于对应的 XML 映射文件中。
     * </p>
     *
     * @param page       分页对象
     * @param name       (未使用，但保留参数位置)
     * @param merchantId 商家ID
     * @return 分页后的评论列表
     */
    IPage<Comment> findPage(Page<Comment> page, @Param("name") String name, @Param("merchantId") Long merchantId);
} 