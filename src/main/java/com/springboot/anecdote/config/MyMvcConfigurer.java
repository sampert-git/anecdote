package com.springboot.anecdote.config;

import com.springboot.anecdote.interceptor.UserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Class AppConfig
 * Description //TODO 应用配置类（实现的WebMvcConfigurer接口有多个可选实现的default方法）
 * Date 2020/9/21 20:41
 * @author Sampert
 * @version 1.0
 */
@Configuration
public class MyMvcConfigurer implements WebMvcConfigurer {

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration=registry.addInterceptor(new UserInterceptor());
        registration.addPathPatterns("/client/**","/admin/**","/anec/list/*/admin","/anec/list/*/admin/*");
    }

    // 配置静态资源访问路径
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:E:/anecdote-upload/");
    }
}
