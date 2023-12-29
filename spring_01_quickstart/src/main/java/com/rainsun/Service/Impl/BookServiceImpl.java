package com.rainsun.Service.Impl;

import com.rainsun.Dao.BookDao;
import com.rainsun.Dao.Impl.BookDaoImpl;
import com.rainsun.Service.BookService;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class BookServiceImpl implements BookService, InitializingBean, DisposableBean {
    private BookDao bookDao;

    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public void save() {
        System.out.println("Book service save");
        bookDao.save();
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("service init");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("service destroy");
    }
}
