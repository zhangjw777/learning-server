package com.learning.trade.config;

import com.learning.trade.entity.Order;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.DelayQueue;

/**
 * 订单延迟队列配置
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Configuration
public class OrderDelayQueueConfig {

    @Bean
    public DelayQueue<Order> orderDelayQueue() {
        return new DelayQueue<>();
    }

}
