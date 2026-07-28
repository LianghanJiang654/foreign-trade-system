package com.foreigntrade.foreign_trade_system.controller;

import com.foreigntrade.foreign_trade_system.service.SeckillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/seckill")
public class SeckillController {

    @Autowired
    private SeckillService seckillService;

    @PostMapping("/{productId}")
    public String seckill(@PathVariable Integer productId, @RequestParam Integer quantity) {
        return seckillService.trySeckill(productId, quantity);
    }
}
