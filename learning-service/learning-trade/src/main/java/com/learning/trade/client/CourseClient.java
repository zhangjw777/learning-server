package com.learning.trade.client;

import com.learning.common.entity.Result;
import com.learning.trade.entity.Course;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 课程服务接口
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@FeignClient("course-service")
public interface CourseClient {

    /**
     * 通过ID查询单个课程
     *
     * @param id 课程ID
     * @return 单个课程
     */
    @GetMapping("courses/{id}")
    Result<Course> queryCourse(@PathVariable("id") Long id);

    /**
     * 更新用户-课程关系
     *
     * @param courseId   课程ID
     * @param username 用户名
     * @return 单个课程
     */
    @PutMapping("courses/registration")
    Result<Course> updateCourse(@RequestParam Long courseId, @RequestParam String username);

}