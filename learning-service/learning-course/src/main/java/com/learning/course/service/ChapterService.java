package com.learning.course.service;

import com.learning.course.entity.Chapter;

import java.util.List;

/**
 * 章节服务接口
 *
 * @author 张家伟
 * @since 2025/04/04
 */
public interface ChapterService {

    /**
     * 通过ID查询单个章节
     *
     * @param id 章节ID
     * @return 单个章节
     */
    Chapter queryById(Long id);

    /**
     * 查询所有章节
     *
     * @return 章节列表
     */
    List<Chapter> list();

    /**
     * 通过课程ID查询所有章节 含章节内的所有内容
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<Chapter> listByCourseId(Long courseId);

    /**
     * 通过课程ID查询所有章节信息 不含课程内容 仅作展示哪些章节
     *
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<Chapter> listInfoByCourseId(Long courseId);

    /**
     * 新增章节
     *
     * @param chapter 章节实例
     * @return 章节实例
     */
    Chapter create(Chapter chapter);

    /**
     * 更新章节
     *
     * @param chapter 章节实例
     * @return 章节实例
     */
    Chapter update(Chapter chapter);

    /**
     * 删除章节
     *
     * @param id 章节ID
     * @return 是否成功
     */
    boolean delete(Long id);



}
