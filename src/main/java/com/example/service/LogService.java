package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.auth0.jwt.JWT;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.Log;
import com.example.entity.User;
import com.example.mapper.LogMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * 系统日志服务类
 */
@Service
public class LogService extends ServiceImpl<LogMapper, Log> {

    @Resource
    private LogMapper logMapper;

    @Resource
    private HttpServletRequest request;

    @Resource
    private UserService userService;

    /**
     * 从HTTP请求中获取当前登录的用户信息
     * @return User 或 null（如果token无效或不存在）
     */
    public User getUser() {
        try {
            String token = request.getHeader("token");
            String username = JWT.decode(token).getAudience().get(0);
            return userService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUsername, username));
        } catch (Exception e) {
            // 捕获所有异常，例如token不存在或解析失败
            return null;
        }
    }

    /**
     * 记录日志（自动获取当前用户）
     * @param content 日志内容
     */
    public void log(String content) {
        Log log = new Log();
        log.setUser(getUser().getUsername());
        log.setTime(DateUtil.formatDateTime(new Date()));
        log.setIp(getIpAddress());
        log.setContent(content);
        save(log);
    }

    /**
     * 记录日志（手动指定用户）
     * @param username 用户名
     * @param content  日志内容
     */
    public void log(String username, String content) {
        Log log = new Log();
        log.setUser(username);
        log.setTime(DateUtil.formatDateTime(new Date()));
        log.setIp(getIpAddress());
        log.setContent(content);
        save(log);
    }


    /**
     * 从HttpServletRequest中获取客户端的IP地址。
     * <p>
     * 该方法会依次尝试从 'x-forwarded-for', 'Proxy-Client-IP', 'WL-Proxy-Client-IP'
     * 这些常见的代理头中获取IP，如果都失败，则使用 request.getRemoteAddr()。
     * </p>
     * @return 客户端IP地址字符串
     */
    public String getIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

}
