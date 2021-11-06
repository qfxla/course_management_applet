package com.haotongxue.config;

import com.haotongxue.interceptor.WeChatLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Autowired
    WeChatLoginInterceptor weChatLoginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //设需要验证登录的路径
        registry.addInterceptor(weChatLoginInterceptor).addPathPatterns("/**/authority/**");
    }
}
