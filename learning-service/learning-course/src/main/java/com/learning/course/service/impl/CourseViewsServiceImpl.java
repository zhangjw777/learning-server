package com.learning.course.service.impl;

import com.learning.course.entity.Course;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CourseViewsServiceImpl {
    private final StringRedisTemplate stringRedisTemplate;
    private static final String KEY_PREFIX = "course:views:";
    public CourseViewsServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 增加课程访问次数
     * @param course
     * @return
     */
    public Long incrementViewCount(Course course) {
        String key = KEY_PREFIX + course.getName();
        // 使用 INCR 命令，原子性地将 key 的值加 1
        // 如果 key 不存在，会先初始化为 0 再执行 INCR 操作
        return stringRedisTemplate.opsForValue().increment(key);
    }

    /**
     * 查询课程访问次数
     * @param course
     * @return
     */
    public Integer queryViewCountsOfCourse(Course course) {
        if (course == null || course.getId() == null) {
            return 0; // 或者根据业务逻辑处理
        }
        String redisKey = KEY_PREFIX+course.getName();
        String value = (String)(stringRedisTemplate.opsForValue().get(redisKey));
        try {
            // 如果 Redis 中没有这个 key，get返回null
            // 如果有值，它应该是数字字符串，需要解析
            return (value == null) ? 0 : Integer.parseInt(value);
        } catch (NumberFormatException e) {
            // 如果值不是有效的数字字符串（理论上不应该发生，除非有其他程序错误地写入了非数字），记录错误并返回 0
            log.error("无法解析课程 {} (ID: {}) 的访问次数 '{}'", course.getName(), course.getId(), value, e);
            return 0;
        }
    }


}
