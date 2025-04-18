package com.learning.auth.controller;

import com.learning.auth.client.UserClient;
import com.learning.auth.entity.User;
import com.learning.common.entity.Result;
import com.learning.common.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@RestController
@RequestMapping("oauth/user")
public class UserController {

    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @GetMapping
    public Result<User> getUser(@RequestHeader("Authorization") String token) {
        String username = JwtUtil.getUsername(token);
        Result<User> result = userClient.queryUser(username);
        result.getData().setPassword(null);
        return result;
    }

    @PostMapping
    public Result<?> registerUser(@RequestBody User user) {
        return userClient.createUser(user);
    }

}