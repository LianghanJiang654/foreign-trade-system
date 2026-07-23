package com.foreigntrade.foreign_trade_system.repository;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
}

