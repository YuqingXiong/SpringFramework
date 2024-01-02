package com.rainsun.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("jdbc.properties")
@ComponentScan("com.rainsun")
@Configuration
public class SpringConfig {
}
