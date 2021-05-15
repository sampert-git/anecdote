package com.springboot.anecdote.config;

import com.springboot.anecdote.interceptor.UserInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MVC配置类（实现的WebMvcConfigurer接口有多个可选实现的default方法）
 *
 * @author Sampert
 * @version 1.0
 * @date 2020/9/21 20:41
 */
@Configuration
public class MyMvcConfigurer implements WebMvcConfigurer {

    /** 文件（图片）上传路径 */
    @Value("${my.properties.file-upload-location}")
    private String fileUploadLocation;

    /**
     * 添加拦截器
     * @param registry 拦截器注册机
     * @return void
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new UserInterceptor());
        registration.addPathPatterns("/client/**", "/admin/**", "/anec/list/*/admin", "/anec/list/*/admin/*");
    }

    /**
     * 添加静态资源处理器
     * @param registry 静态资源处理器注册机
     * @return void
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations(fileUploadLocation);
    }
}
