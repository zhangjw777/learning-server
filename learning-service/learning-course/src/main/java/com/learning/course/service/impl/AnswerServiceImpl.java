package com.learning.course.service.impl;

import com.learning.course.client.UserClient;
import com.learning.course.dao.AnswerDao;
import com.learning.course.dao.QuestionDao;
import com.learning.course.entity.Answer;
import com.learning.course.entity.User;
import com.learning.course.service.AnswerService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 答案服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Service
public class AnswerServiceImpl implements AnswerService {

    private final AnswerDao answerDao;
    private final QuestionDao questionDao;
    private final UserClient userClient;

    public AnswerServiceImpl(AnswerDao answerDao, QuestionDao questionDao, UserClient userClient) {
        this.answerDao = answerDao;
        this.questionDao = questionDao;
        this.userClient = userClient;
    }

    @Override
    public Answer queryById(Long id) {
        return answerDao.selectById(id);
    }

    @Override
    public PageInfo<Answer> listByQuestionId(int pageNum, int pageSize, Long questionId) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(answerDao.listByQuestionId(questionId));
    }

    @Override
    public PageInfo<Answer> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(answerDao.list());
    }

    @Override
    public Answer create(Answer answer, String username) {
        //创建评论
        User user = new User();
        user.setUsername(username);
        answer.setAuthor(user);
        LocalDateTime now = LocalDateTime.now();
        answer.setCreateTime(now);
        answer.setUpdateTime(now);
        answerDao.insert(answer);
        //增加这个评论作者的积分
        userClient.addPointsByUsername(username, 10);
        //更新问题评论数
        questionDao.increaseAnswerCountByQuestionId(answer.getQuestionId());
        return answer;
    }

    @Override
    public Answer update(Answer answer) {
        answer.setUpdateTime(LocalDateTime.now());
        answerDao.update(answer);
        return answerDao.selectById(answer.getId());
    }

    @Override
    public boolean delete(Long id) {
        questionDao.decreaseAnswerCountByAnswerId(id);
        return answerDao.delete(id) > 0;
    }

}

