package com.learning.course.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticDao {

    /**
     * 查询所有教师的用户名和姓名
     * @return 教师列表，每个元素是一个包含 "username" 和 "fullName" 的 Map
     */
    List<Map<String, String>> selectAllTeacherNames();

    /**
     * 根据教师用户名查询其发布的所有课程 ID
     * @param teacherUsername 教师用户名
     * @return 课程 ID 列表
     */
    List<Long> selectCourseIdsByTeacherUsername(@Param("teacherUsername") String teacherUsername);

    /**
     * 根据课程 ID 列表统计这些课程下的总提问数
     * @param courseIds 课程 ID 列表
     * @return 总提问数
     */
    Long countQuestionsByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 根据课程 ID 列表查询学习这些课程的所有学生用户名 (去重)
     * @param courseIds 课程 ID 列表
     * @return 学生用户名列表
     */
    List<String> selectStudentUsernamesByCourseIds(@Param("courseIds") List<Long> courseIds);

    /**
     * 根据课程 ID 列表和学生用户名列表，统计这些学生因完成这些课程而获得的总积分奖励
     * @param courseIds 课程 ID 列表
     * @param studentUsernames 学生用户名列表
     * @return 总积分奖励
     */
    Integer sumCompletedCoursePointsReward(@Param("courseIds") List<Long> courseIds, @Param("studentUsernames") List<String> studentUsernames);

    /**
     * 根据课程 ID 列表查询课程对应的教师用户名
     * @param courseIds 课程 ID 列表
     * @return 课程ID到教师用户名的映射列表，每个元素包含 "courseId" 和 "teacherUsername"
     */
    List<Map<String, Object>> selectTeacherUsernameByCourseIds(@Param("courseIds") List<Long> courseIds);
}
