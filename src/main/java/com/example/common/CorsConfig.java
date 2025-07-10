package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 用于解决前后端分离项目中的跨域问题
 */
@Configuration
public class CorsConfig {

    // 当前跨域请求最大有效时长。这里默认1天
    private static final long MAX_AGE = 24 * 60 * 60;

    /**
     * 构建CORS配置对象
     * @return CorsConfiguration
     */
    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 1. 设置允许的访问源地址，"*"表示允许所有
        corsConfiguration.addAllowedOrigin("*");
        // 2. 设置允许的访问源请求头，"*"表示允许所有
        corsConfiguration.addAllowedHeader("*");
        // 3. 设置允许的访问源请求方法，"*"表示允许所有
        corsConfiguration.addAllowedMethod("*");
        // 4. 设置跨域预检请求的最大有效时长
        corsConfiguration.setMaxAge(MAX_AGE);
        return corsConfiguration;
    }

    /**
     * 创建CORS过滤器
     * @return CorsFilter
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有接口配置跨域设置
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }
}
