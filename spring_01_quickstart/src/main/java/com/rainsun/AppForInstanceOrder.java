package com.rainsun;

import com.rainsun.Dao.OrderDao;
import com.rainsun.factory.OrderDaoFactory;

public class AppForInstanceOrder {
    public static void main(String[] args) {
        OrderDaoFactory orderDaoFactory = new OrderDaoFactory();
        OrderDao orderDao = orderDaoFactory.getOrderDao();
        orderDao.save();
    }
}
