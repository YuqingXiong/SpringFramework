package com.rainsun.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:jdbc.properties")
@ComponentScan("com.rainsun")
@Import({JdbcConfig.class, MybatisConfig.class})
public class SpringConfig {

}
