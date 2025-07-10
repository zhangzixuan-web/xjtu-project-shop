package com.example.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Web MVC 配置类
 * 用于配置拦截器等
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 添加拦截器配置
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        // 注册认证拦截器
//        registry.addInterceptor(authInterceptor())
//                // 指定需要拦截的路径，这里是/api/下的所有路径
//                .addPathPatterns("/api/**")
//                // 指定不需要拦截的路径，例如登录和注册接口
//                .excludePathPatterns("/api/user/login", "/api/user/register");
    }

    /**
     * 将AuthInterceptor注入到Spring容器中
     * @return AuthInterceptor
     */
    @Bean
    public AuthInterceptor authInterceptor() {
        return new AuthInterceptor();
    }
}
