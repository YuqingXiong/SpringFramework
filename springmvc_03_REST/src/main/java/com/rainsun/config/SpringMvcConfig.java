package com.rainsun.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

// 3. 创建 springmvc 的配置文件，加载 controller 对应的 bean
@Configuration
@ComponentScan("com.rainsun.controller")
// 开启 json 数据类型自动转换
@EnableWebMvc
public class SpringMvcConfig {
}
