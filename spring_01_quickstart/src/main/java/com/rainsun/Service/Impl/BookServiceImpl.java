package com.rainsun.Service.Impl;

import com.rainsun.Dao.BookDao;
import com.rainsun.Dao.Impl.BookDaoImpl;
import com.rainsun.Service.BookService;

public class BookServiceImpl implements BookService {
    private BookDao bookDao = new BookDaoImpl();
    @Override
    public void save() {
        System.out.println("Book service save");
        bookDao.save();
    }
}
