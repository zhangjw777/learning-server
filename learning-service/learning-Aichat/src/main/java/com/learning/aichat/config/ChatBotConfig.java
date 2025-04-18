package com.learning.aichat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class ChatBotConfig {

    @Value("${aliyun.dashscope.threadpool.core-size:2}")
    private int corePoolSize;

    @Value("${aliyun.dashscope.threadpool.max-size:10}")
    private int maximumPoolSize;

    @Value("${aliyun.dashscope.threadpool.keep-alive-seconds:60}")
    private long keepAliveTime;

    @Value("${aliyun.dashscope.threadpool.queue-capacity:50}")
    private int queueCapacity;

    // 将线程池定义为 Bean
    @Bean("chatbotThreadPoolExecutor") // 给 Bean 一个明确的名字
    public ThreadPoolExecutor chatbotThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy() // 或者选择其他拒绝策略，如 CallerRunsPolicy
        );
    }
}
