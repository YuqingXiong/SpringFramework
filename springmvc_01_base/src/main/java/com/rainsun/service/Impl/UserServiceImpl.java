package com.rainsun.service.Impl;

import com.rainsun.domain.User;
import com.rainsun.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public void save(User user) {
        System.out.println("user service ...");
    }
}
