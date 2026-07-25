package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import com.foreigntrade.foreign_trade_system.model.Product;
import com.foreigntrade.foreign_trade_system.repository.OrderItemRepository;
import com.foreigntrade.foreign_trade_system.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<OrderItem> getAllOrderItem() {
        return orderItemRepository.findAll();
    }

    public OrderItem createOrderItem(OrderItem orderItem) {
        Integer productId = orderItem.getProduct().getId();
        Product product = productRepository.findById(productId).get();
        if (orderItem.getQuantity() > product.getStockQuantity()) {
            throw new RuntimeException("库存不足");
        }
        product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());

        try {
            productRepository.save(product);
        } catch (OptimisticLockException e) {
            throw new RuntimeException("库存正在被其他订单更改，请重试！");
        }
        orderItem.setProduct(product);
        return orderItemRepository.save(orderItem);
    }
}