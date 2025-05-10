package com.learning.course.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.course.entity.Roadmap;
import com.learning.course.service.IRoadmapService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 张家伟
 * @since 2025-05-06
 */
@RestController
@RequestMapping("/roadmaps")
public class RoadmapController {
    private final IRoadmapService roadmapService;

    public RoadmapController(IRoadmapService roadmapService) {
        this.roadmapService = roadmapService;
    }

    @GetMapping("/list")
    public Result<List<Roadmap>> list() {
        return Result.of(ResultStatus.SUCCESS, roadmapService.list());
    }

    @GetMapping("category/{categoryId}")
    public Result<List<Roadmap>> listByCategoryId(@PathVariable Integer categoryId) {
        return Result.of(ResultStatus.SUCCESS, roadmapService.listByCategoryId(categoryId));
    }

    @PostMapping
    public Result<Boolean> create(@RequestBody Roadmap roadmap) {
        roadmap.setCreateTime(LocalDateTime.now());
        roadmap.setUpdateTime(LocalDateTime.now());
        return Result.of(ResultStatus.SUCCESS, roadmapService.save(roadmap));
    }
    @PutMapping
    public Result<Boolean> update(@RequestBody Roadmap roadmap) {
        LambdaQueryWrapper<Roadmap> lambdaQueryWrapper = new LambdaQueryWrapper<Roadmap>().eq(Roadmap::getId, roadmap.getId());
        roadmap.setUpdateTime(LocalDateTime.now());
        return Result.of(ResultStatus.SUCCESS, roadmapService.update(roadmap, lambdaQueryWrapper));
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> delete(@PathVariable Integer id) {
        LambdaQueryWrapper<Roadmap> lambdaQueryWrapper = new LambdaQueryWrapper<Roadmap>().eq(Roadmap::getId, id);
        return Result.of(ResultStatus.SUCCESS, roadmapService.remove(lambdaQueryWrapper));
    }

}
