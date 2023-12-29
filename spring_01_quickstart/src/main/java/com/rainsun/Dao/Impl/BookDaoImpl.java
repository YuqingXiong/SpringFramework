package com.rainsun.Dao.Impl;

import com.rainsun.Dao.BookDao;

public class BookDaoImpl implements BookDao{
    private String databaseName;
    private int connectionNum;

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public void setConnectionNum(int connectionNum) {
        this.connectionNum = connectionNum;
    }

    private BookDaoImpl() {
//        System.out.println("book dao constructor is running");
    }
    @Override
    public void save() {
        System.out.println("Book dao save" + databaseName + ", " + connectionNum);
    }

    // bean 初始化对应的操作
    public void init(){
        System.out.println("init ...");
    }
    // bean销毁前的操作
    public void destroy(){
        System.out.println("destroy ...");
    }
}
