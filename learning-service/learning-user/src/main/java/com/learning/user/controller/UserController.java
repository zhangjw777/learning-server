package com.learning.user.controller;

import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.common.entity.ResultStatus;
import com.learning.user.client.CourseClient;
import com.learning.user.entity.Certificate;
import com.learning.user.entity.Course;
import com.learning.user.entity.Note;
import com.learning.user.entity.User;
import com.learning.user.service.UserService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 用户控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("users")
public class UserController {

    private final UserService userService;
    private final CourseClient courseClient;

    public UserController(UserService userService, CourseClient courseClient) {
        this.userService = userService;
        this.courseClient = courseClient;
    }

    @GetMapping("{username}")
    public Result<User> queryUser(@PathVariable String username) {
        User user = userService.queryByUsername(username);
        return Result.of(ResultStatus.SUCCESS, user);
    }

    @GetMapping("{username}/courses")
    public Result<Page<Course>> listCoursesOfUser(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                                  @PathVariable("username") String studentName) {
        return courseClient.listCoursesByStudentName(pageNum, pageSize, studentName);
    }

    @GetMapping("{username}/notes")
    public Result<Page<Note>> listNotesOfUser(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize,
                                              @PathVariable String username) {
        return courseClient.listNotesByUsername(pageNum, pageSize, username);
    }


    @GetMapping("{username}/certificates")
    public Result<Page<Certificate>> listCertificatesByUserName(@RequestParam(defaultValue = "1") Integer pageNum, @RequestParam(defaultValue = "10") Integer pageSize,
                                                                @PathVariable String username) {
        return courseClient.listCertificatesByUsername(pageNum, pageSize, username);
    }

    @GetMapping
    public Result<Page<User>> listUser(@RequestParam(defaultValue = "1") int pageNum, @RequestParam(defaultValue = "10") int pageSize) {
        PageInfo<User> pageInfo = userService.list(pageNum, pageSize);
        return Result.of(ResultStatus.SUCCESS, Page.of(pageInfo.getList(), pageInfo.getTotal()));
    }
    @GetMapping("role")
    public Result<List<User>> listUserByRole(@RequestParam String roleName) {
        List<User> users = userService.listUserByRole(roleName);
        return Result.of(ResultStatus.SUCCESS, users);
    }
    /**
     *
     * @param username
     * @param pointsToAdd 传负数则减积分
     * @return
     */
    @PutMapping("{username}/points")
    public Result<User> addPointsByUsername(@PathVariable String username, @RequestParam int pointsToAdd) {
        userService.addPoints(username, pointsToAdd);
        return Result.of(ResultStatus.SUCCESS, null);
    }

    @PostMapping
    public Result<User> createUser(@RequestBody @Validated({User.Create.class}) User user) {
        return Result.of(ResultStatus.SUCCESS, userService.create(user));
    }

    @PutMapping
    public Result<User> updateUser(@RequestBody @Validated({User.Update.class}) User user) {
        User updatedUser = userService.update(user);
        return Result.of(ResultStatus.SUCCESS, updatedUser);
    }

    @DeleteMapping("{username}")
    public ResultStatus deleteUser(@PathVariable String username) {
        userService.delete(username);
        return ResultStatus.SUCCESS;
    }

@GetMapping("pointsRank")
    public Result<List<Map<String, Object>>> queryPointsMap() {
        List<Map<String, Object>> pointsRank = userService.getPointsMap();
        return Result.of(ResultStatus.SUCCESS, pointsRank);
    }
}