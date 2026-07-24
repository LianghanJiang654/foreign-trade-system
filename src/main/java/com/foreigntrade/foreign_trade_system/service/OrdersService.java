package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.Orders;
import com.foreigntrade.foreign_trade_system.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }


    public Orders createOrders( Orders orders) {
        return ordersRepository.save(orders);
    }
}
