package com.rainsun.config;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.support.AbstractDispatcherServletInitializer;

// 4. 定义一个 servlet 容器启动的配置类。用于加载 spring的配置
public class ServletContainerInitConfig extends AbstractDispatcherServletInitializer {
    // 加载 SpringMVC 容器配置
    @Override
    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringMvcConfig.class);
        return ctx;
    }

    // 设置那些请求归属于 SpringMVC 处理
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    // 加载 spring容器配置
    @Override
    protected WebApplicationContext createRootApplicationContext() {
        AnnotationConfigWebApplicationContext ctx = new AnnotationConfigWebApplicationContext();
        ctx.register(SpringConfig.class);
        return ctx;
    }
}
