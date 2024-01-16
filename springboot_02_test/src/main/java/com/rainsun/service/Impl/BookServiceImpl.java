package com.rainsun.service.Impl;

import com.rainsun.service.BookService;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Override
    public void save() {
        System.out.println("book service ...");
    }
}
