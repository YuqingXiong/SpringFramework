package com.rainsun.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MyAdvice {

    // 切入点
    @Pointcut("execution(void com.rainsun.dao.BookDao.update())")
    public void pt(){}

    // 切面，定义通知和切入点的连接方式
//    @Before("pt()")
    public void before(){
        System.out.println("before advice...");
    }

//    @After("pt()")
    public void after(){
        System.out.println("after advice...");
    }

    @Around("pt()")
    public void around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("around before advice...");
        pjp.proceed();
        System.out.println("around after advice...");
    }


}
