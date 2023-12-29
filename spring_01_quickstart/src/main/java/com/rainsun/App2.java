package com.rainsun;

import com.rainsun.Dao.BookDao;
import com.rainsun.Service.BookService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App2 {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        BookDao bookDao = (BookDao)ctx.getBean("bookDao");
        bookDao.save();
    }
}
