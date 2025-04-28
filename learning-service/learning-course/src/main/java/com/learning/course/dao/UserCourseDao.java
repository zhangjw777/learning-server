package com.learning.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.course.entity.UserCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserCourseDao extends BaseMapper<UserCourse> {
    /**
     * 统计用户课程表中课程的学生数 有误报不用管
     */
    List<Map<String, Long>> statisticUserCourse();

    List<Map<String, Object>> getCourseCompletionStats();
}
