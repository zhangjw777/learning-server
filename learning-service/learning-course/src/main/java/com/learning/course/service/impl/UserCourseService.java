package com.learning.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learning.course.client.UserClient;
import com.learning.course.dao.ChapterDao;
import com.learning.course.dao.CourseDao;
import com.learning.course.dao.UserCourseDao;
import com.learning.course.entity.UserCourse;
import com.learning.course.service.ICertificateService;
import com.learning.course.service.IUserCourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCourseService extends ServiceImpl<UserCourseDao, UserCourse> implements IUserCourseService {

    private final UserCourseDao userCourseDao;
    private final CourseDao courseDao;
    private final ChapterDao chapterDao;
    private final ICertificateService certificateService;
    private final UserClient userClient;
    @Override
    public void completeCourse(String userName, Long courseId) {
        log.info("设置课程已完成");
        Integer chapterCount = courseDao.selectById(courseId).getChapterCount();
        //判断章节是否为空以及是否完成
        if(chapterCount==null||chapterCount<1||checkCourseCompleted(userName, courseId))
            return;
        certificateService.createCertificate(userName, courseId);
        // 更新用户课程表，设置完成状态和完成时间
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserName, userName)
                .eq(UserCourse::getCourseId, courseId);
        UserCourse userCourse = new UserCourse();
        userCourse.setIsCompleted(1);
        userCourse.setCurrentChapter(-1L);
        userCourse.setCompletionTime(LocalDateTime.now());
        userCourseDao.update(userCourse, queryWrapper);
        //调用用户客户端远程调用，更新用户积分
       userClient.addPointsByUsername(userName, courseDao.selectById(courseId).getPointsReward());
        log.info("用户积分增加");
    }

    @Override
    public Long queryCurrentChapterId(String userName, Long courseId)  {
        if (checkCourseCompleted(userName, courseId)) {
            return -1L;//表示已完成,可给前端或者其他业务判断而需要查checkCourseCompleted
        }
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserName, userName)
                .eq(UserCourse::getCourseId, courseId);
        return userCourseDao.selectOne(queryWrapper).getCurrentChapter();
    }

    @Override
    public Boolean checkCourseCompleted(String userName, Long courseId)  {
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserCourse::getUserName, userName)
                .eq(UserCourse::getCourseId, courseId);
        UserCourse userCourse = userCourseDao.selectOne(queryWrapper);
        if (userCourse==null)
            throw new RuntimeException("用户课程记录不存在");
        if (userCourse.getIsCompleted()==1){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public boolean updateUserChapter(Long courseId, Long chapterId, String userName) {
        //更新用户课程表的新当前章节，让用户观看下一章节
        //先查询下一个章节，然后把这个章节设置到用户课程表中
        LambdaQueryWrapper<UserCourse> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.
                eq(UserCourse::getCourseId,courseId)
                .eq(UserCourse::getUserName,userName);
        Long lastChapterId = chapterId;
        Long  nextChapterId = chapterDao.selectNextChapterId(courseId,lastChapterId);
        if (nextChapterId==null){
            log.info("用户已经观看完该课程的所有章节");
            this.completeCourse(userName,courseId);
            return true;
        }
        UserCourse userCourse = new UserCourse();
        userCourse.setCurrentChapter(nextChapterId);
        return userCourseDao.update(userCourse, queryWrapper)>0;
    }
}
