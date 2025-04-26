package com.learning.trade.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.trade.entity.Order;
import com.learning.trade.service.OrderService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("{id}")
    public Result<Order> queryOrder(@PathVariable("id") Long id) {
        return Result.of(ResultStatus.SUCCESS, orderService.queryById(id));
    }

    @GetMapping
    public Result<Page<Order>> listOrder(@RequestParam(defaultValue = "1") int pageNumber, @RequestParam(defaultValue = "10") int pageSize
            , @RequestParam(required = false) String username) {
        PageInfo<Order> pageInfo;
        if (username != null)
            pageInfo = orderService.listByUsername(pageNumber, pageSize, username);
        else {
            pageInfo = orderService.list(pageNumber, pageSize);

        }
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Order> createOrder(@RequestBody Order order, @RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsername(token);
        Order existingOrder = orderService.queryUnpaidByUsernameAndProductId(username, order.getProductId());
        if (existingOrder != null) {
            return Result.of(ResultStatus.SUCCESS, existingOrder);
        }
        return Result.of(ResultStatus.SUCCESS, orderService.create(order, username));
    }

    @PutMapping
    public Result<Order> updateOrder(@RequestBody Order order) {
        return Result.of(ResultStatus.SUCCESS, orderService.update(order));
    }

    /*@PutMapping("{id}/cancel")
    public ResultStatus cancelOrder(@PathVariable("id") Long id) {
        boolean success = orderService.delete(id);
        if (!success) {
            return ResultStatus.ARGUMENT_NOT_VALID;
        }
        return ResultStatus.SUCCESS;*//*
    }*/

    @DeleteMapping("{id}")
    public ResultStatus deleteOrder(@PathVariable("id") Long id) {
        orderService.delete(id);
        return ResultStatus.SUCCESS;
    }

}
