package com.rainsun.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

// 3. 创建 springmvc 的配置文件，加载 controller 对应的 bean
@Configuration
@ComponentScan("com.rainsun.controller")
public class SpringMvcConfig {
}
