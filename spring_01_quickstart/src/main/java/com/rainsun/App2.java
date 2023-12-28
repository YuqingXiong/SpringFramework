package com.rainsun;

import com.rainsun.Dao.BookDao;
import com.rainsun.Service.BookService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App2 {
    public static void main(String[] args) {
        // IoC 容器获取
        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        // 获取 Bean
//        BookDao bookDao = (BookDao)ctx.getBean("bookDao");
//        bookDao.save();
        BookService bookService = (BookService)ctx.getBean("bookService");
        bookService.save();
    }
}
