package com.foreigntrade.foreign_trade_system.controller;
import com.foreigntrade.foreign_trade_system.model.Orders;
import com.foreigntrade.foreign_trade_system.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping
    public List<Orders> getAllOrders() {
        return ordersService.getAllOrders();
    }
    @PostMapping
        public Orders createOrders(@RequestBody Orders orders){
        return ordersService.createOrders(orders);

    }
}

