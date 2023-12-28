package com.rainsun.Dao.Impl;

import com.rainsun.Dao.BookDao;

public class BookDaoImpl implements BookDao {
    private BookDaoImpl() {
        System.out.println("book dao constructor is running");
    }
    @Override
    public void save() {
        System.out.println("Book dao save");
    }
}
