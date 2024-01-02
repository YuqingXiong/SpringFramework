package com.rainsun.Dao.Impl;

import com.rainsun.Dao.BookDao;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;

@Repository
public class BookDaoImpl implements BookDao{

    @Value("${name}")
    private String name;

    @Override
    public void save() {
        System.out.println("Book dao save");
    }
}
