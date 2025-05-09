package com.learning.course.service.impl;

import com.learning.course.dao.EvaluationDao;
import com.learning.course.entity.Course;
import com.learning.course.entity.Evaluation;
import com.learning.course.entity.User;
import com.learning.course.service.CourseService;
import com.learning.course.service.EvaluationService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 评价服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Service
public class EvaluationServiceImpl implements EvaluationService {

    private final EvaluationDao evaluationDao;
    private final CourseService courseService;

    public EvaluationServiceImpl(EvaluationDao evaluationDao, CourseService courseService) {
        this.evaluationDao = evaluationDao;
        this.courseService = courseService;
    }

    @Override
    public Evaluation queryById(Long id) {
        return evaluationDao.selectById(id);
    }

    @Override
    public PageInfo<Evaluation> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(evaluationDao.list());
    }

    @Override
    public PageInfo<Evaluation> listByCourseId(int pageNum, int pageSize, Long courseId) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(evaluationDao.listByCourseId(courseId));
    }

    @Override
    public Evaluation create(Evaluation evaluation, String username) {
        LocalDateTime now = LocalDateTime.now();
        evaluation.setCreateTime(now);
        evaluation.setUpdateTime(now);
        Course course = new Course();
        User user = new User();
        user.setUsername(username);
        evaluation.setAuthor(user);
        evaluationDao.insert(evaluation);
        Long courseId = evaluation.getCourseId();
        course.setId(courseId);
        Integer averageScore = evaluationDao.selectAverageScoreByCourseId(courseId);
        course.setAverageScore(averageScore);
        courseService.update(course);
        return evaluation;
    }

    @Override
    public Evaluation update(Evaluation evaluation) {
        evaluation.setUpdateTime(LocalDateTime.now());
        evaluationDao.update(evaluation);
        return evaluationDao.selectById(evaluation.getId());
    }

    @Override
    public boolean delete(Long id) {
        return evaluationDao.delete(id) > 0;
    }

}