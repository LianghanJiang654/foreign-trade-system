package com.foreigntrade.foreign_trade_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String sku;

    private String name;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Version
    private Integer version;
    // 在这里接着写 name 字段

    // 在这里接着写 unitPrice 字段,记得用 @Column(name = "unit_price") 对应数据库的下划线命名

    // 在这里接着写 stockQuantity 字段,同样需要 @Column(name = "stock_quantity")

    // 在这里接着写 createdAt 字段,类型是 LocalDateTime,需要 @Column(name = "created_at")
}

