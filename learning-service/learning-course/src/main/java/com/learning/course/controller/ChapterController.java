package com.learning.course.controller;

import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.course.entity.Chapter;
import com.learning.course.service.ChapterService;
import com.learning.course.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 章节控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("chapters")
public class ChapterController {

    private final ChapterService chapterService;
    private final CourseService courseService;

    public ChapterController(ChapterService chapterService, CourseService courseService) {
        this.chapterService = chapterService;
        this.courseService = courseService;
    }

    @GetMapping("{id}")
    public Result<Chapter> queryChapter(@PathVariable("id") Long id) {
        return Result.of(ResultStatus.SUCCESS, chapterService.queryById(id));
    }

    @GetMapping
    public Result<List<Chapter>> listChapter() {
        return Result.of(ResultStatus.SUCCESS, chapterService.list());
    }

    @PostMapping
    public Result<Chapter> createChapter(@RequestBody Chapter chapter) {
        courseService.incrementChapterCountOfCourse(chapter.getCourseId());
        return Result.of(ResultStatus.SUCCESS, chapterService.create(chapter));
    }

    @PutMapping
    public Result<Chapter> updateChapter(@RequestBody Chapter chapter) {
        return Result.of(ResultStatus.SUCCESS, chapterService.update(chapter));
    }

    @DeleteMapping("{id}")
    public ResultStatus deleteChapter(@PathVariable("id") Long id) {
        chapterService.delete(id);
        return ResultStatus.SUCCESS;
    }

}
