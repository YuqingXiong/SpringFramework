package com.rainsun;

import com.rainsun.Service.BookService;
import com.rainsun.Service.Impl.BookServiceImpl;

public class App 
{
    public static void main( String[] args ){
        BookService bookService = new BookServiceImpl();
        bookService.save();
    }
}
