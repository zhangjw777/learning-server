package com.learning.course.dao;

import com.learning.course.entity.Answer;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 答案数据库访问层
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Mapper
public interface AnswerDao {

    /**
     * 通过ID查询单个答案
     *
     * @param id 答案ID
     * @return 单个答案
     */
    Answer selectById(Long id);

    /**
     * 查询所有答案
     *
     * @return 答案列表
     */
    List<Answer> list();

    /**
     * 通过课程ID查询所有答案
     *
     * @param questionId 课程ID
     * @return 答案列表
     */
    List<Answer> listByQuestionId(Long questionId);

    /**
     * 新增答案
     *
     * @param answer 答案实例
     * @return 答案实例
     */
    int insert(Answer answer);

    /**
     * 更新答案
     *
     * @param answer 答案实例
     * @return 影响行数
     */
    int update(Answer answer);

    /**
     * 删除答案
     *
     * @param id 答案ID
     * @return 影响行数
     */
    int delete(Long id);

    /**
     * 通过问题ID删除答案
     * @param id
     * @return
     */
    int deleteByQuestionId(Long id);

    List<Map<String, Object>> statisticCountOf7Days();

    Integer count();
}
