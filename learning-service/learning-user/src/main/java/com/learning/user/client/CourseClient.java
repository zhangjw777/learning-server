package com.learning.user.client;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.user.entity.Course;
import com.learning.user.entity.Note;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
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
     * 通过学生用户名查询课程
     *
     * @param pageNum     页码
     * @param pageSize    页大小
     * @param studentName 学生用户名
     * @return 课程列表
     */
    @GetMapping("courses")
    Result<Page<Course>> listCoursesByStudentName(@RequestParam int pageNum, @RequestParam int pageSize, @RequestParam String studentName);

    /**
     * 通过用户名查询笔记
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @param username 用户名
     * @return 笔记列表
     */
    @GetMapping("notes")
    Result<Page<Note>> listNotesByUsername(@RequestParam int pageNum, @RequestParam int pageSize, @RequestParam String username);

}
