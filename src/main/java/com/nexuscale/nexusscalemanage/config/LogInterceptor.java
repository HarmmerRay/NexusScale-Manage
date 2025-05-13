package com.nexuscale.nexusscalemanage.config;

import com.nexuscale.nexusscalemanage.entity.Log;
import com.nexuscale.nexusscalemanage.service.LogService;
import com.nexuscale.nexusscalemanage.util.EncryptionDecryption;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.sql.Timestamp;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class LogInterceptor implements HandlerInterceptor{
    @Autowired
    private LogService logService;

    // 请求处理前的鉴权逻辑
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求路径、请求参数、用户ID、IP、方法、等信息
        // 获取请求路径
        String requestPath = request.getRequestURI();

        // 获取请求参数
        Map<String, String> requestParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            requestParams.put(paramName, paramValue);
        }

        // 获取请求方式
        String requestMethod = request.getMethod();

        // 获取 IP 地址
        String clientIp = request.getRemoteAddr();

        // 获取用户 ID（这里假设用户 ID 存储在会话中）
        String userId = null;
        if (request.getSession().getAttribute("userId") != null) {
            userId = request.getSession().getAttribute("userId").toString();
        }
        if (userId == null) {
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies){
                if (cookie.getName().equals("userId")){
                    userId = cookie.getValue();
                    request.getSession().setAttribute("userId", userId);
                }
            }
        }

//        System.out.println("userId = " + userId);
        // 当前时间
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // 打印获取到的信息
//        System.out.println("当前时间：" + timestamp);
//        System.out.println("请求路径: " + requestPath);
//        System.out.println("请求参数: " + requestParams);
//        System.out.println("用户 ID: " + userId);
//        System.out.println("IP 地址: " + clientIp);
//        System.out.println("请求方式: " + requestMethod);
        String requestParam = requestParams.toString();
        if (requestPath.equals("/user/upload_avatar")){
            requestParam = "图片base64";
        }
        System.out.println(requestPath);
        Log log = new Log(timestamp.toLocalDateTime(),requestPath,requestParam,userId,clientIp,requestMethod);
        System.out.println(log);
        logService.insertLog(log);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }

    private boolean isValidToken(String token) {
        // 实际开发中应调用认证服务或数据库验证 Token 是否有效
        return "valid-token".equals(token);
    }
}
