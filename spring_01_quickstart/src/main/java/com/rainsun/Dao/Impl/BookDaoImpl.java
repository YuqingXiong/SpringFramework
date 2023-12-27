package com.rainsun.Dao.Impl;

import com.rainsun.Dao.BookDao;

public class BookDaoImpl implements BookDao {
    @Override
    public void save() {
        System.out.println("Book dao save");
    }
}
