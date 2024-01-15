package com.rainsun.service.Impl;

import com.rainsun.controller.Code;
import com.rainsun.dao.BookDao;
import com.rainsun.domain.Book;
import com.rainsun.exception.BusinessException;
import com.rainsun.exception.SystemException;
import com.rainsun.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookDao bookDao;
    @Override
    public boolean save(Book book) {
        return bookDao.save(book) > 0;
    }

    @Override
    public boolean update(Book book) {
        return bookDao.update(book) > 0;
    }

    @Override
    public boolean delete(Integer id) {
        return bookDao.delete(id) > 0;
    }

    @Override
    public Book getById(Integer id) {
        if(id == 1){
            throw new BusinessException(Code.BUSINESS_ERR, "输入参数错误");
        }
        // 将可能出现的异常进行包装，转换为自定义异常
        // 一般不会下面这样写，只是为了包装异常为自己的异常
//        try {
//            int i = 1/0;
//        } catch (Exception e) {
//            throw new SystemException(Code.SYSTEM_ERR, "服务器错误", e);
//        }
        return bookDao.getById(id);
    }

    @Override
    public List<Book> getAll() {
        return bookDao.getAll();
    }
}
