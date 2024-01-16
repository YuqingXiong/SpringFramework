package com.rainsun.service;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BookServiceTest {
    @Autowired
    private BookService bookService;

    @Test
    public void testGetById(){
        System.out.println(bookService.getById(2));
    }
    @Test
    public void testGetAll(){
        System.out.println(bookService.getAll());
    }
}
