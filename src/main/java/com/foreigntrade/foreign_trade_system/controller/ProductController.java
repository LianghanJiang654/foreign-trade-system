package com.foreigntrade.foreign_trade_system.controller;

import com.foreigntrade.foreign_trade_system.model.Product;
import com.foreigntrade.foreign_trade_system.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/{id}/sync-stock")
    public String syncStockToCache(@PathVariable Integer id) {
        productService.initStockToCache(id);
        return "库存已同步到Redis";
    }
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

}