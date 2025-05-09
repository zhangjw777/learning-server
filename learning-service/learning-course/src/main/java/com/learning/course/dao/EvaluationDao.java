package com.learning.course.dao;

import com.learning.course.entity.Evaluation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 评价数据库访问层
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Mapper
public interface EvaluationDao {

    /**
     * 通过ID查询单个评价
     *
     * @param id 评价ID
     * @return 单个评价
     */
    Evaluation selectById(Long id);

    /**
     * 查询所有评价
     *
     * @return 评价列表 更新时间降序
     */
    List<Evaluation> list();

    /**
     * 通过课程ID查询所有评价
     *
     * @param courseId 课程ID
     * @return 评价列表
     */
    List<Evaluation> listByCourseId(Long courseId);

    /**
     * 新增评价
     *
     * @param evaluation 评价实例
     * @return 评价实例
     */
    int insert(Evaluation evaluation);

    /**
     * 更新评价
     *
     * @param evaluation 评价实例
     * @return 影响行数
     */
    int update(Evaluation evaluation);

    /**
     * 删除评价
     *
     * @param id 评价ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 删除评价
     *
     * @param courseId 课程ID
     * @return 平均评分
     */
    Integer selectAverageScoreByCourseId(Long courseId);

    Integer count();

    List<Map<String, Object>> statisticCountOf7Days();
}