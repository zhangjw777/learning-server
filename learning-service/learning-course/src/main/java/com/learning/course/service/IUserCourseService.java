package com.learning.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learning.course.entity.UserCourse;

import java.util.List;
import java.util.Map;

public interface IUserCourseService extends IService<UserCourse> {
    /**
     * 用户完成整个课程
     * @param userName
     * @param courseId 课程ID
     */
    void completeCourse(String userName, Long courseId) ;

    /**
     * 通过用户课程表查询当前的章节id
     * @param userName
     * @param id
     * @return 如果没找到课程，则返回Null，如果找到，则返回章节ID，如果返回1，代表已经完成课程
     */
    Long queryCurrentChapterId(String userName, Long id) ;

    Boolean checkCourseCompleted(String userName, Long courseId) ;

    /**
     * 更新用户-课程关系 设置当前章节
     * @param courseId
     * @param chapterId 要设置的新章节id
     * @param userName
     * @return
     */
    boolean updateUserChapter(Long courseId, Long chapterId, String userName);

    /**
     * 计算课程有多少学生学习过
     */
    /**
     * 统计用户课程表 课程用户的个数
     *
     * @return 键：课程id,值：用户个数
     */
    List<Map<String, Long>> statisticUserCourse();

    /**
     * 获取课程用户数量的Map对象 {课程id:用户数量}
     * @return Map对象
     */
    Map<Long, Long> getCourseUserCountMap();
}
