package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.repository.OrderItemRepository;
import com.foreigntrade.foreign_trade_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SeckillService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillService.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductRepository productRepository;

    public String trySeckill(Integer productId, Integer quantity) {
        String stockKey = "seckill:stock:" + productId;

        Long remainingStock = redisTemplate.opsForValue().decrement(stockKey, quantity);

        if (remainingStock == null || remainingStock < 0) {
            redisTemplate.opsForValue().increment(stockKey, quantity);
            logger.warn("秒杀失败，库存不足，productId={}", productId);
            return "秒杀失败，库存不足";
        }

        logger.info("秒杀成功，productId={}, 剩余库存={}", productId, remainingStock);
        return "秒杀成功，剩余库存=" + remainingStock;
    }
}
