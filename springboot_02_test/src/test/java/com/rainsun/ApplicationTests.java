package com.rainsun;

import com.rainsun.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApplicationTests {

    @Autowired
    private BookService bookService;

    @Test
    void saveTest() {
        bookService.save();
    }

}
