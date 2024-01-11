package com.rainsun.service.impl;

import com.rainsun.dao.AccountDao;
import com.rainsun.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountDao accountDao;

    @Override
    public void transfer(String out, String in, Double money) {
        accountDao.outMoney(out, money);
        int x = 1/0;
        accountDao.inMoney(in, money);
    }
}
