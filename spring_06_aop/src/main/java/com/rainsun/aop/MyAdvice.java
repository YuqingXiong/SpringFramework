package com.rainsun.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class MyAdvice {

    // 切入点
    @Pointcut("execution(* com.rainsun.dao.BookDao.findName(..))")
    public void pt(){}

    // 切面，定义通知和切入点的连接方式
    @Before("pt()")
    public void before(JoinPoint jp){
        Object[] args = jp.getArgs();
        System.out.println(Arrays.toString(args));
        System.out.println("before advice...");
    }

//    @After("pt()")
    public void after(){
        System.out.println("after advice...");
    }

//    @Around("pt()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println("around before advice...");
        Object res = pjp.proceed();
        System.out.println("around after advice...");
        return res;
    }


}
