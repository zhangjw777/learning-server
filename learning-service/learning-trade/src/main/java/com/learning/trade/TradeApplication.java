package com.learning.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 交易服务启动类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class TradeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TradeApplication.class, args);
    }

}
