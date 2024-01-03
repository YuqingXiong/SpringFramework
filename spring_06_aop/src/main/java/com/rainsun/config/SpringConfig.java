package com.rainsun.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan("com.rainsun")
@EnableAspectJAutoProxy
public class SpringConfig {

}
