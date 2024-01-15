package com.rainsun.config;

import com.rainsun.controller.interceptor.ProjectInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
@ComponentScan({"com.rainsun.controller"})
@EnableWebMvc
public class SpringMvcConfig implements WebMvcConfigurer {

    @Autowired
    private ProjectInterceptor projectInterceptor;

    // 添加拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器：里面写了拦截时我们想做啥
        // 添加拦截路径：表示拦截哪些访问路径
        registry.addInterceptor(projectInterceptor)
                .addPathPatterns("/books","/books/*");

    }
}
