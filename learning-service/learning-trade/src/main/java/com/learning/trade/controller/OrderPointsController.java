package com.learning.trade.controller;

import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.trade.client.CourseClient;
import com.learning.trade.client.UserClient;
import com.learning.trade.entity.Order;
import com.learning.trade.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("orders/points")
@RequiredArgsConstructor
public class OrderPointsController {
    private final OrderService orderService;
    private final UserClient userClient;
    private final CourseClient courseClient;

    @PostMapping
    public Result<Order> newOrder(@RequestBody Order order, @RequestHeader("Authorization") String token){
       String username = JwtUtil.getUsername(token);
        Order existingOrder = orderService.queryUnpaidByUsernameAndProductId(username, order.getProductId());
        if (existingOrder != null) {
            return Result.of(ResultStatus.SUCCESS, existingOrder);
        }
        Order orderBack = orderService.create(order, username);
        return Result.of(ResultStatus.SUCCESS, orderBack);
    }
    @PostMapping("pay")
    public Result<Order> payOrder(@RequestBody Order order, @RequestHeader("Authorization") String token){
        String username = JwtUtil.getUsername(token);
        //如果发送过来的订单状态不是1且令牌用户名不是发送过来的用户名，则返回错误
        if(order.getStatus()!=1&&order.getUsername()!=username){
            return Result.of(ResultStatus.ARGUMENT_NOT_VALID, null);
        }
        Order orderBack = orderService.update(order);
        //扣除用户积分
        int pointToMinus = -order.getPointsPrice();
        userClient.addPointsByUsername(username, pointToMinus);
        //登记课程
        courseClient.updateCourse(order.getProductId(), username);
        return Result.of(ResultStatus.SUCCESS, orderBack);
    }
}
