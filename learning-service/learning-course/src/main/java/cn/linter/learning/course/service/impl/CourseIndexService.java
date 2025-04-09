package cn.linter.learning.course.service.impl;
import cn.linter.learning.course.dao.CourseDao;
import cn.linter.learning.course.dao.CourseSearchDao;
import cn.linter.learning.course.entity.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * es课程索引服务
 */
@Service
public class CourseIndexService {
    private static final Logger log = LoggerFactory.getLogger(CourseIndexService.class);

    // 注入访问主数据库的 Repository/Mapper
    @Autowired
    private CourseDao courseDbRepository; // 确保这个 Bean 存在且能工作

    // 注入 Elasticsearch Repository
    @Autowired
    private CourseSearchDao courseSearchDao;

    /**
     * 从主数据库读取所有课程数据并索引到 Elasticsearch
     * 建议在数据量大时分批处理
     */
    // @Transactional(readOnly = true) // 如果从数据库读取需要事务保护
    public void indexAllCoursesFromDatabase() {
        log.info("开始从主数据库加载课程数据...");
        List<Course> coursesFromDb;
        try {
            // --- 从你的主数据库获取数据 ---
            coursesFromDb = courseDbRepository.list(true,"create_time"); // 或者你的 MyBatis 查询方法
            // --- 数据获取完成 ---

            log.info("从数据库加载了 {} 条课程数据。", coursesFromDb.size());
        } catch (Exception e) {
            log.error("从数据库加载课程数据时出错", e);
            return; // 加载失败，则不进行索引
        }


        if (coursesFromDb != null && !coursesFromDb.isEmpty()) {
            log.info("开始将课程数据索引到 Elasticsearch...");
            try {
                // --- 将数据写入 Elasticsearch ---
                // saveAll 是批量操作，效率比单条 save 高
                courseSearchDao.saveAll(coursesFromDb);
                // --- 数据写入完成 ---
                log.info("成功将 {} 条课程数据索引到 Elasticsearch 索引 '{}'。", coursesFromDb.size(), "courses"); // 硬编码或从实体获取索引名
            } catch (Exception e) {
                log.error("索引课程数据到 Elasticsearch 时出错", e);
                // 这里可以考虑更复杂的错误处理，比如记录失败的ID等
            }
        } else {
            log.info("数据库中没有课程数据需要索引。");
        }
    }

    /**
     * 检查 Elasticsearch 中是否已有课程数据
     * @return 如果索引存在且有数据则返回 true，否则 false
     */
    public boolean hasDataInElasticsearch() {
        try {
            // 检查索引是否存在并且文档数大于0
            // 注意：如果索引不存在，count() 可能会抛异常或返回0，具体行为取决于版本和客户端
            // 更稳妥的方式是先检查索引是否存在 indexOps().exists()
            // 但对于简单场景，直接 count() 通常也够用
            log.info("es中的数据：{}",courseSearchDao.findAll());
            return courseSearchDao.count() > 0;
        } catch (Exception e) {
            // 捕获可能的异常，例如连接问题或索引不存在（某些版本的行为）
            log.warn("检查 Elasticsearch 数据时出错或索引 'courses' 可能不存在: {}", e.getMessage());
            return false;
        }
    }
}

