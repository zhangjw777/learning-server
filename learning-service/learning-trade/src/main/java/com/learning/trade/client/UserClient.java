package com.learning.trade.client;


import com.learning.common.entity.Result;
import com.learning.trade.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

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


}
