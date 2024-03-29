package com.rainsun.controller;

import com.rainsun.exception.BusinessException;
import com.rainsun.exception.SystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 声明这是一个异常处理的方法
@RestControllerAdvice
public class ProjectExceptionAdvice {
    // 指明处理的异常类型
    @ExceptionHandler(SystemException.class)
    public Result doSystemException(SystemException ex){
        // 记录日志
        // 发送消息给维护人员
        // 发送邮件给开发人员
        return new Result(ex.getCode(), null, ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    public Result doBusinessException(BusinessException ex){
        return new Result(ex.getCode(), null, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex){
        // 记录日志
        // 发送消息给维护人员
        // 发送邮件给开发人员
        return new Result(Code.SYSTEM_UNKNOW_ERR, null, "系统繁忙，稍后再试");
    }

}
