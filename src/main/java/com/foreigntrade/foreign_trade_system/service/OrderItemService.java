package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import com.foreigntrade.foreign_trade_system.repository.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    public List<OrderItem> getAllOrderItem() {
        return orderItemRepository.findAll();


    }


    public OrderItem createOrderItem( OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }
}
