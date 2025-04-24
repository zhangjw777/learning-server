package com.learning.course;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

/**
 * 课程服务启动类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@EnableFeignClients
@EnableElasticsearchRepositories
@SpringBootApplication
@EnableDiscoveryClient
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }

}