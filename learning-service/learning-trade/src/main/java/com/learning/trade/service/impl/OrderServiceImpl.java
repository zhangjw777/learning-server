package com.learning.trade.service.impl;

import com.learning.trade.client.CourseClient;
import com.learning.trade.dao.OrderDao;
import com.learning.trade.entity.Course;
import com.learning.trade.entity.Order;
import com.learning.trade.service.OrderService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.DelayQueue;

/**
 * 订单服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderDao orderDao;
    private final CourseClient courseClient;
    private final DelayQueue<Order> orderDelayQueue;

    public OrderServiceImpl(OrderDao orderDao, CourseClient courseClient, DelayQueue<Order> orderDelayQueue) {
        this.orderDao = orderDao;
        this.courseClient = courseClient;
        this.orderDelayQueue = orderDelayQueue;
    }

    @Override
    public Order queryById(Long id) {
        return orderDao.selectById(id);
    }

    @Override
    public Order queryByTradeNo(String tradeNo) {
        return orderDao.selectByTradeNo(tradeNo);
    }

    @Override
    public Order queryUnpaidByUsernameAndProductId(String username, Long productId) {
        return orderDao.queryUnpaidByUsernameAndProductId(username, productId);
    }

    @Override
    public PageInfo<Order> list(int pageNumber, int pageSize) {
        PageHelper.startPage(pageNumber, pageSize);
        return PageInfo.of(orderDao.list());
    }

    @Override
    public List<Order> listUnpaid() {
        return orderDao.listUnpaid();
    }

    @Override
    public Order create(Order order, String username) {
        //获取课程信息并设置订单信息
        Course course = courseClient.queryCourse(order.getProductId()).getData();
        order.setTradeNo(UUID.randomUUID().toString().replaceAll("-", ""));
        order.setPrice(course.getPrice());
        order.setPointsPrice(course.getPointsPrice());
        order.setProductName(course.getName());
        order.setUsername(username);
        long current = System.currentTimeMillis();
        order.setCloseMilliseconds(current + 1800000);
        LocalDateTime now = LocalDateTime.now();
        order.setCreateTime(now);
        order.setUpdateTime(now);
        order.setStatus(0);
        //插入订单信息
        orderDao.insert(order);
        orderDelayQueue.add(order);
        return order;
    }

    @Override
    public Order update(Order order) {
        order.setUpdateTime(LocalDateTime.now());
        orderDao.update(order);
        if (order.getStatus() != null && order.getStatus() != 0) {
            orderDelayQueue.removeIf(delayOrder -> delayOrder.getId().equals(order.getId()));
        }
        return order;
    }

    @Override
    public boolean delete(Long id) {
        orderDelayQueue.removeIf(delayOrder -> delayOrder.getId().equals(id));
        return orderDao.delete(id) > 0;
    }

}

