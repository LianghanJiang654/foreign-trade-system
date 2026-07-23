package com.foreigntrade.foreign_trade_system.repository;

import com.foreigntrade.foreign_trade_system.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Integer> {
}
