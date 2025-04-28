package com.learning.course.client;


import com.learning.common.entity.Page;
import com.learning.common.entity.Result;
import com.learning.course.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient("user-service")
public interface UserClient {
    /**
     * 修改积分
     *
     * @param username
     * @param pointsToAdd
     * @return
     */
    @PutMapping("/users/{username}/points")
    Result<User> addPointsByUsername(@PathVariable String username, @RequestParam int pointsToAdd);

    @GetMapping("/users/pointsRank")
    Result<List<Map<String,Object>>> getPointsMap();

    @GetMapping("/users/role")
    Result<List<User>> listUserByRole(String RoleName);
}
