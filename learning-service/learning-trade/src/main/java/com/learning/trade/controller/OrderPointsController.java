package com.learning.trade.controller;

import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.trade.entity.Order;
import com.learning.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("orders/points")
@RequiredArgsConstructor
public class OrderPointsController {
    private final OrderService orderService;

    @GetMapping
    public Result<Order> newOrder(@RequestBody Order order, @RequestHeader("Authorization") String token){
       String username = JwtUtil.getUsername(token);
        Order orderBack = orderService.create(order, username);
        return Result.of(ResultStatus.SUCCESS, orderBack);
    }
}
