package com.rainsun.service;

import org.springframework.transaction.annotation.Transactional;

public interface AccountService {
    @Transactional
    public void transfer(String out, String in, Double money);
}
