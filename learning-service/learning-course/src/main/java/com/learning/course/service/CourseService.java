package com.learning.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.course.entity.Category;
import com.learning.course.entity.Course;
import com.github.pagehelper.PageInfo;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 课程服务接口
 *
 * @author 张家伟
 * @since 2025/04/04
 */
public interface CourseService extends IService<Course> {

    /**
     * 通过ID查询单个课程
     *
     * @param id 课程ID
     * @return 单个课程
     */
    Course queryById(Long id);

    /**
     * 查询用户-课程关系
     *
     * @param id       课程ID
     * @param username 用户名
     * @return 单个课程
     */
    Course queryByIdAndName(Long id, String username);

    /**
     * 分页查询所有课程
     *
     * @param pageNum  页号
     * @param pageSize 页大小
     * @param approved 审核通过
     * @param orderBy  排序
     * @return 课程列表
     */
    PageInfo<Course> list(int pageNum, int pageSize, Boolean approved, String orderBy);

    /**
     * 通过教师用户名分页查询所有课程
     *
     * @param pageNum     页号
     * @param pageSize    页大小
     * @param teacherName 教师用户名
     * @return 课程列表
     */
    PageInfo<Course> listByTeacherName(int pageNum, int pageSize, String teacherName);

    /**
     * 通过学生用户名分页查询所有课程
     *
     * @param pageNum     页号
     * @param pageSize    页大小
     * @param studentName 学生用户名
     * @return 课程列表
     */
    PageInfo<Course> listByStudentName(int pageNum, int pageSize, String studentName);

    /**
     * 通过分类ID分页查询所有课程
     *
     * @param pageNum    页号
     * @param pageSize   页大小
     * @param categoryId 分类ID
     * @param orderBy    排序
     * @return 课程列表
     */
    PageInfo<Course> listByCategoryId(int pageNum, int pageSize, Integer categoryId, String orderBy);

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
     * @param course   课程实例
     * @param username 课程老师用户名
     * @return 课程实例
     */
    Course create(Course course, String username);

    /**
     * 更新课程
     *
     * @param course 课程实例
     * @return 课程实例
     */
    Course update(Course course);

    /**
     * 删除课程
     *
     * @param id 课程ID
     * @return 是否成功
     */
    boolean delete(Long id);


    /**
     * 插入用户-课程关系
     *
     * @param username 用户名
     * @param courseId 课程ID
     * @return 课程实例
     */
    Boolean insertRegistration(String username, Long courseId);

    /**
     * 通过关键词搜索课程
     *
     * @param keyword  关键词
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 课程列表
     */
    Page<Course> search(String keyword, int pageNum, int pageSize);

    /**
     * 同步课程列表到Elasticsearch中
     */
    void synchronize();

    void incrementChapterCountOfCourse(Long courseId);

}