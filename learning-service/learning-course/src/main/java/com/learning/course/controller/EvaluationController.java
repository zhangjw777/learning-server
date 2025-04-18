package com.learning.course.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.course.entity.Evaluation;
import com.learning.course.service.EvaluationService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

/**
 * 评价控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping("{id}")
    public Result<Evaluation> queryEvaluation(@PathVariable("id") Long id) {
        return Result.of(ResultStatus.SUCCESS, evaluationService.queryById(id));
    }

    @GetMapping
    public Result<Page<Evaluation>> listEvaluation(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        PageInfo<Evaluation> pageInfo = evaluationService.list(pageNum, pageSize);
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Evaluation> createEvaluation(@RequestBody Evaluation evaluation, @RequestHeader("Authorization") String token) {
        return Result.of(ResultStatus.SUCCESS, evaluationService.create(evaluation, JwtUtil.getUsername(token)));
    }

    @PutMapping
    public Result<Evaluation> updateEvaluation(@RequestBody Evaluation evaluation) {
        return Result.of(ResultStatus.SUCCESS, evaluationService.update(evaluation));
    }

    @DeleteMapping("{id}")
    public ResultStatus deleteEvaluation(@PathVariable("id") Long id) {
        evaluationService.delete(id);
        return ResultStatus.SUCCESS;
    }

}
