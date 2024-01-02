package com.rainsun;

import com.rainsun.Dao.BookDao;
import com.rainsun.Service.BookService;
import com.rainsun.config.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppForAnnotation {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
        BookService bookService = (BookService)ctx.getBean("bookServiceImpl");
        bookService.save();
        ctx.close(); //关闭容器
    }
}

/**
 * 输出：
 * Book dao init
 * Book dao save
 * Book dao destroy
 */