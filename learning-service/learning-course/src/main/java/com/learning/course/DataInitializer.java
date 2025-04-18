package com.learning.course; // 你的主包或配置包名

import com.learning.course.service.impl.CourseIndexService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private CourseIndexService courseIndexService;

    @Override
    public void run(String... args) throws Exception {
        log.info("应用程序启动完成，开始检查并执行数据初始化...");

        // 检查 Elasticsearch 中是否已经有数据，避免每次启动都重复导入
        if (!courseIndexService.hasDataInElasticsearch()) {
            log.info("Elasticsearch 索引 'courses' 为空或不存在，开始执行一次性数据导入...");
            courseIndexService.indexAllCoursesFromDatabase();
        } else {
            log.info("Elasticsearch 索引 'courses' 中已存在数据，跳过初始导入。");
        }
    }
}