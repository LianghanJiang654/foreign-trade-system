package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.OrderItem;
import com.foreigntrade.foreign_trade_system.model.Product;
import com.foreigntrade.foreign_trade_system.repository.OrderItemRepository;
import com.foreigntrade.foreign_trade_system.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderItemServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemService orderItemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createOrderItem_withSufficientStock_shouldSucceed() {
        Product product = new Product();
        product.setId(1);
        product.setStockQuantity(100);

        Product requestProduct = new Product();
        requestProduct.setId(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setProduct(requestProduct);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(any())).thenReturn(product);
        when(orderItemRepository.save(any())).thenReturn(orderItem);

        OrderItem result = orderItemService.createOrderItem(orderItem);

        assertEquals(90, product.getStockQuantity());
        verify(productRepository).save(product);
        verify(orderItemRepository).save(orderItem);
    }

    @Test
    void createOrderItem_withInsufficientStock_shouldThrowException() {
        Product product = new Product();
        product.setId(1);
        product.setStockQuantity(5);

        Product requestProduct = new Product();
        requestProduct.setId(1);

        OrderItem orderItem = new OrderItem();
        orderItem.setQuantity(10);
        orderItem.setProduct(requestProduct);

        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderItemService.createOrderItem(orderItem);
        });

        assertEquals("库存不足", exception.getMessage());
    }
}