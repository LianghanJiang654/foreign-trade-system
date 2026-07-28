package com.foreigntrade.foreign_trade_system.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.foreigntrade.foreign_trade_system.model.Product;
import com.foreigntrade.foreign_trade_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Integer id) {
        String cacheKey = "product:" + id;

        String cachedValue = redisTemplate.opsForValue().get(cacheKey);
        if (cachedValue != null) {
            logger.info("缓存命中，productId={}", id);
            return parseFromCache(cachedValue);
        }

        logger.info("缓存未命中，查询数据库，productId={}", id);
        Product product = productRepository.findById(id).get();

        redisTemplate.opsForValue().set(cacheKey, serializeToCache(product), Duration.ofMinutes(10));

        return product;
    }

    private String serializeToCache(Product product) {
        try {
            return objectMapper.writeValueAsString(product);
        } catch (JacksonException e) {
            throw new RuntimeException("序列化失败", e);
        }
    }
    public void initStockToCache(Integer productId) {
        Product product = productRepository.findById(productId).get();
        String stockKey = "seckill:stock:" + productId;
        redisTemplate.opsForValue().set(stockKey, String.valueOf(product.getStockQuantity()));
        logger.info("秒杀库存已同步到Redis，productId={}, stock={}", productId, product.getStockQuantity());
    }
    private Product parseFromCache(String cachedValue) {
        try {
            return objectMapper.readValue(cachedValue, Product.class);
        } catch (JacksonException e) {
            throw new RuntimeException("反序列化失败", e);
        }
    }
}