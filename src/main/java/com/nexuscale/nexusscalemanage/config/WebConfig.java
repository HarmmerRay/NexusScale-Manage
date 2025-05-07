package com.nexuscale.nexusscalemanage.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;
    @Autowired
    private LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 拦截所有路径，排除无需鉴权的接口（如登录接口）
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**") // 拦截所有接口
                .excludePathPatterns("/user/register", "/user/get_code", "/user/verify_token"); // 排除无需鉴权的路径
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/**");
    }
}