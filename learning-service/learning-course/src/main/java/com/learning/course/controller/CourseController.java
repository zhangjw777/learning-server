package com.learning.course.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.exception.BusinessException;
import com.learning.common.utils.JwtUtil;
import com.learning.course.entity.*;
import com.learning.course.service.*;
import com.github.pagehelper.PageInfo;
import com.learning.course.service.impl.CourseViewsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 课程控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("courses")
public class CourseController {

    private final CourseService courseService;
    private final ChapterService chapterService;
    private final QuestionService questionService;
    private final NoteService noteService;
    private final EvaluationService evaluationService;
    private final CourseViewsServiceImpl courseViewsService;


    @GetMapping("{id}")
    public Result<Course> queryCourse(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        Course course;
        if (token == null) {
            course = courseService.queryById(id);
        } else {
            course = courseService.queryByIdAndName(id, JwtUtil.getUsername(token));
        }
        courseViewsService.incrementViewCount(course);
        course.setViewCounts(courseViewsService.queryViewCountsOfCourse(course));
        return Result.of(ResultStatus.SUCCESS, course);
    }

    /**
     * 查询当前用户应该继续学习的课程章节信息 需要用到表： 用户-章节， 课程，
     *
     * @param id    课程id
     * @param token
     * @return 返回当前用户应该继续学习的课程章节对象
     */
    @GetMapping("{id}/currentChapter")
    public Result<Chapter> queryCurrentChapter(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null)
            throw new BusinessException(ResultStatus.UNAUTHORIZED);//登录用户才可能继续学习 如果未购买课程，前端会拦截的
        String userName = JwtUtil.getUsername(token);
        Long chapterId = courseService.queryCurrentChapterId(userName, id);
        if (chapterId == null) {
            return Result.of(ResultStatus.SUCCESS, null);
        } else if (chapterId == -1L) {
            //表示课程已完成
            Chapter chapter = new Chapter();
            chapter.setId(-1L);
            return Result.of(ResultStatus.SUCCESS, chapter);
        } else {
            Chapter chapter = chapterService.queryById(chapterId);
            return Result.of(ResultStatus.SUCCESS, chapter);
        }
    }

    @GetMapping("{id}/chapters")
    public Result<List<Chapter>> listChaptersOfCourse(@PathVariable("id") Long id, @RequestHeader(value = "Authorization", required = false) String token) {
        List<Chapter> chapters;
        if (token == null) {
            chapters = chapterService.listInfoByCourseId(id);
        } else {
            Course course = courseService.queryByIdAndName(id, JwtUtil.getUsername(token));
            if (course.getRegistered()) {
                chapters = chapterService.listByCourseId(id);
            } else {
                chapters = chapterService.listInfoByCourseId(id);
            }
        }
        return Result.of(ResultStatus.SUCCESS, chapters);
    }

    @GetMapping("{id}/questions")
    public Result<Page<Question>> listQuestionsOfCourse(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                                        @PathVariable("id") Long id, @RequestParam(defaultValue = "create_time") String orderBy) {
        PageInfo<Question> pageInfo = questionService.listByCourseId(pageNum, pageSize, id, orderBy);
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @GetMapping("{id}/notes")
    public Result<Page<Note>> listNotesOfCourse(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                                @PathVariable("id") Long id, @RequestParam(defaultValue = "false") Boolean onlyOwn,
                                                @RequestHeader(value = "Authorization", required = false) String token) {
        PageInfo<Note> pageInfo;
        if (!onlyOwn || token == null) {
            pageInfo = noteService.listByCourseId(pageNum, pageSize, id);
        } else {
            pageInfo = noteService.listByCourseIdAndUsername(pageNum, pageSize, id, JwtUtil.getUsername(token));
        }
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @GetMapping("{id}/evaluations")
    public Result<Page<Evaluation>> listEvaluationsOfCourse(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                                            @PathVariable("id") Long id) {
        PageInfo<Evaluation> pageInfo = evaluationService.listByCourseId(pageNum, pageSize, id);
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @GetMapping
    public Result<Page<Course>> listCourse(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                           @RequestParam(required = false) String teacherName, @RequestParam(required = false) String studentName,
                                           @RequestParam(required = false) Integer categoryId, @RequestParam(defaultValue = "create_time") String orderBy,
                                           @RequestParam(defaultValue = "true") Boolean approved) {
        PageInfo<Course> pageInfo;
        if (teacherName != null) {
            pageInfo = courseService.listByTeacherName(pageNum, pageSize, teacherName);
        } else if (studentName != null) {
            pageInfo = courseService.listByStudentName(pageNum, pageSize, studentName);
        } else if (categoryId != null) {
            pageInfo = courseService.listByCategoryId(pageNum, pageSize, categoryId, orderBy);
        } else {
            pageInfo = courseService.list(pageNum, pageSize, approved, orderBy);
        }
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Course> createCourse(@RequestBody Course course, @RequestHeader("Authorization") String token) {
        return Result.of(ResultStatus.SUCCESS, courseService.create(course, JwtUtil.getUsername(token)));
    }

    @PutMapping
    public Result<Course> updateCourse(@RequestBody Course course, @RequestHeader(value = "Authorization") String token, @RequestParam(required = false) String username) {
        log.info("update course: {}", course);
        return Result.of(ResultStatus.SUCCESS, courseService.update(course));
    }

    /**
     * 用户学习某个课程，课程登记
     *
     * @param courseId
     * @param username
     * @param token
     * @return
     */
    @PutMapping("registration")
    public ResultStatus updateRegistrationOfCourse(@RequestParam Long courseId, @RequestParam(required = false) String username,
                                                   @RequestHeader(value = "Authorization", required = false) String token) {
        boolean success;
        if (username != null) {
            success = courseService.insertRegistration(username, courseId);
        } else {
            success = courseService.insertRegistration(JwtUtil.getUsername(token), courseId);
        }
        if (!success)
            return ResultStatus.USER_NOT_FOUND;
        return ResultStatus.SUCCESS;


    }

    @DeleteMapping("{id}")
    public ResultStatus deleteCourse(@PathVariable("id") Long id) {
        courseService.delete(id);
        return ResultStatus.SUCCESS;
    }

    @GetMapping("/search")
    public Result<Page<Course>> searchCourse(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                                             @RequestParam(value = "pageNum", defaultValue = "0") int pageNum,
                                             @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        org.springframework.data.domain.Page<Course> courses = courseService.search(keyword, pageNum, pageSize);
        return Result.of(ResultStatus.SUCCESS, Page.of(courses.getContent(), courses.getTotalElements()));
    }

    @GetMapping("/synchronize")
    public ResultStatus synchronizeCourse() {
        courseService.synchronize();
        return ResultStatus.SUCCESS;
    }

}