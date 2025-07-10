package com.example.common;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.service.UserService;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 认证授权拦截器
 * 用于对用户请求进行token验证和权限校验
 */
public class AuthInterceptor implements HandlerInterceptor {

    @Resource
    private UserService userService;

    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     *
     * @param request  http请求
     * @param response http响应
     * @param handler  被调用的处理器对象，本质是Controller中被请求的那个方法
     * @return boolean 返回true才会继续执行，返回false则取消当前请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 从请求头中获取token
        String token = request.getHeader("token");
        // 获取请求的URI
        String requestURI = request.getRequestURI();

        // 对Swagger和静态资源的请求直接放行
        if (requestURI.contains("/doc.html") || requestURI.contains("/webjars") || requestURI.contains("/swagger-resources") || requestURI.contains("/v2/api-docs")) {
            return true;
        }

        // 如果token为空，则抛出异常，提示用户重新登录
        if (StrUtil.isBlank(token)) {
            throw new CustomException("401", "未获取到token, 请重新登录");
        }
        String username;
        try {
            // 解码token获取用户名
            username = JWT.decode(token).getAudience().get(0);
        } catch (JWTDecodeException j) {
            // token解码失败，抛出异常
            throw new CustomException("401", "权限验证失败, 请重新登录");
        }
        // 根据用户名查询用户信息
        User user = userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        // 如果用户不存在，则抛出异常
        if (user == null) {
            throw new CustomException("401", "用户不存在, 请重新登录");
        }
        // 使用用户密码作为密钥，创建JWT验证器
        JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(user.getPassword())).build();
        try {
            // 验证token的合法性
            jwtVerifier.verify(token);
        } catch (JWTVerificationException e) {
            // token验证失败，抛出异常
            throw new CustomException("401", "token不合法, 请重新登录");
        }

        // ====== 权限校验 ======
        // 如果请求的是商家相关的接口
        if (requestURI.contains("/merchant")) {
            // 检查用户角色是否为"商家"
            if (!"商家".equals(user.getRole())) {
                // 如果不是商家，则无权限访问
                throw new CustomException("401", "无权限访问");
            }
        }
        
        // 验证通过，放行请求
        return true;
    }

}
