package com.learning.course.dao;

import com.learning.course.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * 课程搜索数据访问层
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Repository
public interface CourseSearchDao extends ElasticsearchRepository<Course, Long> {

    /**
     * 通过名称或简介搜索课程
     *
     * @param name        名称
     * @param description 简介
     * @param page        分页规则
     * @return 课程列表
     */
    Page<Course> findByNameOrDescription(String name, String description, Pageable page);

}