package com.learning.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.learning.course.entity.Category;
import com.learning.course.entity.Course;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 课程数据库访问层
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Mapper
public interface CourseDao extends BaseMapper<Course> {

    /**
     * 通过ID查询单个课程
     *
     * @param id 课程ID
     * @return 单个课程
     */
    Course selectById(Long id);

    /**
     * 查询所有课程
     *
     * @param approved 审核通过
     * @param orderBy  排序
     * @return 课程列表
     */
    List<Course> list(@Param("approved") Boolean approved, @Param("orderBy") String orderBy);

    /**
     * 通过教师用户名分页查询所有课程
     *
     * @param teacherName 教师用户名
     * @return 课程列表
     */
    List<Course> listByTeacherName(String teacherName);

    /**
     * 通过学生用户名分页查询所有课程
     *
     * @param studentName 学生用户名
     * @return 课程列表
     */
    List<Course> listByStudentName(String studentName);

    /**
     * 通过分类ID分页查询所有课程
     *
     * @param categoryId 分类ID
     * @param orderBy    排序
     * @return 课程列表
     */
    List<Course> listByCategoryId(@Param("categoryId") Integer categoryId, @Param("orderBy") String orderBy);

    /**
     * 通过ID查询分类列表
     *
     * @param id 课程ID
     * @return 分类列表
     */
    List<Category> listCategoryById(Long id);

    /**
     * 新增课程
     *
     * @param course 课程实例
     * @return 课程实例
     */
    int insert(Course course);

    /**
     * 更新课程
     *
     * @param course 课程实例
     * @return 影响行数
     */
    int update(Course course);

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 查询用户-课程关系
     *
     * @param username 用户名
     * @param courseId 课程ID
     * @return 是否存在
     */
    boolean selectRegistration(@Param("username") String username, @Param("courseId") Long courseId);

    /**
     * 插入用户-课程关系,并设置当前课程章节为该课程第一个章节的id
     *
     * @param username 用户名
     * @param courseId 课程ID
     * @return 影响行数
     */
    int insertRegistration(@Param("username") String username, @Param("courseId") Long courseId);

    /**
     * 为课程插入分类
     *
     * @param courseId   课程ID
     * @param categories 分类列表
     * @return 影响行数
     */
    int insertCategory(@Param("courseId") Long courseId, @Param("categories") List<Category> categories);

    /**
     * 删除课程的分类
     *
     * @param courseId 课程ID
     * @return 影响行数
     */
    int deleteCategory(@Param("courseId") Long courseId);

    /**
     * 课程数量加1
     *
     * @param courseId 课程ID
     *
     */
    void incrementChapterCount(Long courseId);

    List<Map<String, Object>> statisticCourseOfCategoryCounts();
}