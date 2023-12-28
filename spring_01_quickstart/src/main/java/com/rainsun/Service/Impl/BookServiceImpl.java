package com.rainsun.Service.Impl;

import com.rainsun.Dao.BookDao;
import com.rainsun.Dao.Impl.BookDaoImpl;
import com.rainsun.Service.BookService;

public class BookServiceImpl implements BookService {
    // 去掉 new 的实现方式，改为 DI（依赖注入）
//    private BookDao bookDao = new BookDaoImpl();
    private BookDao bookDao;
    @Override
    public void save() {
        System.out.println("Book service save");
        bookDao.save();
    }

    // 通过调用set方法进行依赖注入
    public void setBookDao(BookDao bookDao) {
        this.bookDao = bookDao;
    }
}
