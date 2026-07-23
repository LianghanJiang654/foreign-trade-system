package com.foreigntrade.foreign_trade_system.repository;

import com.foreigntrade.foreign_trade_system.model.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

}

