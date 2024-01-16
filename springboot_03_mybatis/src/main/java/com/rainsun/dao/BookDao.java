package com.rainsun.dao;

import com.rainsun.domain.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BookDao {
    @Select("select * from ssm_db.tbl_book where id=#{id}")
    public Book getById(Integer id);
}
