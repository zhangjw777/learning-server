package com.learning.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 线程池配置
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Configuration
public class ThreadPoolExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        int processors = Runtime.getRuntime().availableProcessors();
        threadPoolTaskExecutor.setCorePoolSize(processors);
        threadPoolTaskExecutor.setMaxPoolSize(processors);
        threadPoolTaskExecutor.setQueueCapacity(20);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

}
