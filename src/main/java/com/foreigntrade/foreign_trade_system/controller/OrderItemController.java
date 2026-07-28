package com.foreigntrade.foreign_trade_system.controller;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import com.foreigntrade.foreign_trade_system.service.OrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/order-items")
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;


    @GetMapping
    public List<OrderItem> getAllOrderItem(){
        return orderItemService.getAllOrderItem();
    }

    @PostMapping
    public OrderItem createOrderItem(@RequestBody OrderItem orderItem){

        return orderItemService.createOrderItem(orderItem);
    }




}
