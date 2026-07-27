package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import com.foreigntrade.foreign_trade_system.model.Product;
import com.foreigntrade.foreign_trade_system.repository.OrderItemRepository;
import com.foreigntrade.foreign_trade_system.repository.ProductRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class OrderItemService {

    private static final Logger logger = LoggerFactory.getLogger(OrderItemService.class);

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<OrderItem> getAllOrderItem() {
        return orderItemRepository.findAll();
    }

    @Transactional
    public OrderItem createOrderItem(OrderItem orderItem) {
        Integer productId = orderItem.getProduct().getId();
        logger.info("开始创建订单明细，productId={}, quantity={}", productId, orderItem.getQuantity());

        Product product = productRepository.findById(productId).get();

        if (orderItem.getQuantity() > product.getStockQuantity()) {
            logger.warn("库存不足，productId={}, 请求数量={}, 当前库存={}", productId, orderItem.getQuantity(), product.getStockQuantity());
            throw new RuntimeException("库存不足");
        }

        product.setStockQuantity(product.getStockQuantity() - orderItem.getQuantity());

        try {
            productRepository.save(product);
        } catch (OptimisticLockException e) {
            logger.error("库存更新冲突，productId={}", productId);
            throw new RuntimeException("库存正在被其他订单更改，请重试！");
        }

        orderItem.setProduct(product);
        logger.info("订单明细创建成功，productId={}, 扣减后库存={}", productId, product.getStockQuantity());

        return orderItemRepository.save(orderItem);
    }
}