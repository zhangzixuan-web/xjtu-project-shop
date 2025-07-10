package com.example.common;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Mybatis-Plus 配置类
 * 用于配置 Mybatis-Plus 相关插件
 */
@Configuration
public class MybatisPlusConfig {

    /**
     * 配置 Mybatis-Plus 分页插件
     *
     * @return PaginationInterceptor 分页拦截器实例
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

}
