package com.rainsun.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAdvice {

    // 切入点
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    public void pt(){}

    // 切面，定义通知和切入点的连接方式
    @Before("pt()")
    public void method(){
        System.out.println(System.currentTimeMillis());
    }
}
