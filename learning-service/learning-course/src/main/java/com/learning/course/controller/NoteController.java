package com.learning.course.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.common.utils.JwtUtil;
import com.learning.course.client.UserClient;
import com.learning.course.entity.Note;
import com.learning.course.service.NoteService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

/**
 * 笔记控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("notes")
public class NoteController {

    private final NoteService noteService;
    private final UserClient userClient;

    public NoteController(NoteService noteService, UserClient userClient) {
        this.noteService = noteService;
        this.userClient = userClient;
    }

    @GetMapping("{id}")
    public Result<Note> queryNote(@PathVariable("id") Long id) {
        return Result.of(ResultStatus.SUCCESS, noteService.queryById(id));
    }

    @GetMapping
    public Result<Page<Note>> listNote(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                       @RequestParam(required = false) String username) {
        PageInfo<Note> pageInfo;
        if (username == null) {
            pageInfo = noteService.list(pageNum, pageSize);
        } else {
            pageInfo = noteService.listByUsername(pageNum, pageSize, username);
        }
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }

    @PostMapping
    public Result<Note> createNote(@RequestBody Note note, @RequestHeader("Authorization") String token) {
        Note noteResult = noteService.create(note, JwtUtil.getUsername(token));
        if (noteResult!=null)
            userClient.addPointsByUsername(JwtUtil.getUsername(token), 10);
        return Result.of(ResultStatus.SUCCESS,noteResult );
    }

    @PutMapping
    public Result<Note> updateNote(@RequestBody Note note) {
        return Result.of(ResultStatus.SUCCESS, noteService.update(note));
    }

    @DeleteMapping("{id}")
    public ResultStatus deleteNote(@PathVariable("id") Long id) {
        noteService.delete(id);
        return ResultStatus.SUCCESS;
    }

}
