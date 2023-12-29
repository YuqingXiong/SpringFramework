package com.rainsun.factory;

import com.rainsun.Dao.Impl.OrderDaoImpl;
import com.rainsun.Dao.OrderDao;
import org.springframework.beans.factory.FactoryBean;

public class OrderDaoFactory implements FactoryBean<OrderDao> {
    @Override
    public OrderDao getObject() throws Exception {
        return new OrderDaoImpl();
    }
    @Override
    public Class<?> getObjectType() {
        return OrderDao.class;
    }
}
