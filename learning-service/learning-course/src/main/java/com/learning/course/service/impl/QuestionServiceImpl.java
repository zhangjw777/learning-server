package com.learning.course.service.impl;

import com.learning.course.dao.AnswerDao;
import com.learning.course.dao.QuestionDao;
import com.learning.course.entity.Question;
import com.learning.course.entity.User;
import com.learning.course.service.QuestionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 问题服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionDao questionDao;
    private final AnswerDao answerDao;


    @Override
    public Question queryById(Long id) {
        return questionDao.selectById(id);
    }

    @Override
    public PageInfo<Question> listByCourseId(int pageNum, int pageSize, Long courseId, String orderBy) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(questionDao.listByCourseId(courseId, orderBy));
    }

    @Override
    public PageInfo<Question> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(questionDao.list());
    }

    @Override
    public Question create(Question question, String username) {
        User user = new User();
        user.setUsername(username);
        question.setAuthor(user);
        question.setAnswerCount(0);
        LocalDateTime now = LocalDateTime.now();
        question.setCreateTime(now);
        question.setUpdateTime(now);
        questionDao.insert(question);
        return question;
    }

    @Override
    public Question update(Question question) {
        question.setUpdateTime(LocalDateTime.now());
        questionDao.update(question);
        return questionDao.selectById(question.getId());
    }

    @Override
    public boolean delete(Long id) {
        int deleteAnswerCount =answerDao.deleteByQuestionId(id);
        int deleteQuestionCount = questionDao.delete(id);
        return  deleteQuestionCount==1 && deleteAnswerCount>=0;
    }

}

