package com.rainsun.service;

import com.rainsun.domain.Book;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 开启事务
@Transactional
public interface BookService {
    /**
     * 保存一个 Book
     * @param book
     * @return
     */
    public boolean save(Book book);
    public boolean update(Book book);

    public boolean delete(Integer id);

    public Book getById(Integer id);

    public List<Book> getAll();
}
