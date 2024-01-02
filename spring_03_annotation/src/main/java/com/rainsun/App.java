package com.rainsun;

import com.rainsun.Dao.BookDao;
import com.rainsun.Service.BookService;
import com.rainsun.Service.Impl.BookServiceImpl;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App 
{
    public static void main( String[] args ){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        BookDao bookDao = (BookDao)ctx.getBean("bookDao");
        bookDao.save();
        BookService bookService = (BookService)ctx.getBean("bookServiceImpl");
        bookService.save();
    }
}
