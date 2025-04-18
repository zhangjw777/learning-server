package com.learning.course.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.course.entity.Answer;
import com.learning.course.service.AnswerService;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

/**
 * 答案控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("answers")
public class AnswerController {

    private final AnswerService answerService;

    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @GetMapping("{id}")
    public Result<Answer> queryAnswer(@PathVariable("id") Long id) {
        return Result.of(ResultStatus.SUCCESS, answerService.queryById(id));
    }

    @GetMapping
    public Result<Page<Answer>> listAnswer(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        PageInfo<Answer> pageInfo = answerService.list(pageNum, pageSize);
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Answer> createAnswer(@RequestBody Answer answer, @RequestHeader("Authorization") String token) {
        return Result.of(ResultStatus.SUCCESS, answerService.create(answer, JwtUtil.getUsername(token)));
    }

    @PutMapping
    public Result<Answer> updateAnswer(@RequestBody Answer answer) {
        return Result.of(ResultStatus.SUCCESS, answerService.update(answer));
    }

    @Transactional
    @DeleteMapping("{id}")
    public ResultStatus deleteAnswer(@PathVariable("id") Long id) {

        answerService.delete(id);
        return ResultStatus.SUCCESS;
    }

}
