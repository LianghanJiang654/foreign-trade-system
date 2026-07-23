package com.foreigntrade.foreign_trade_system.repository;

import com.foreigntrade.foreign_trade_system.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
