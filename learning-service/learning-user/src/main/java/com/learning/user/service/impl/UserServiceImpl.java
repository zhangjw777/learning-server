package com.learning.user.service.impl;

import com.learning.common.entity.ResultStatus;
import com.learning.common.exception.BusinessException;
import com.learning.user.dao.UserDao;
import com.learning.user.entity.Role;
import com.learning.user.entity.User;
import com.learning.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public User queryByUsername(String username) {
        return userDao.selectByUsername(username);
    }

    @Override
    public PageInfo<User> list(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(userDao.list());
    }

    @Override
    public User create(User user) {
        if (userDao.selectByUsername(user.getUsername()) != null) {
            throw new BusinessException(ResultStatus.USERNAME_EXISTS);
        }
        String rawPassword = user.getPassword();
        if (rawPassword != null) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        if (user.getRole() == null || user.getRole().getId() == null) {
            Role role = new Role();
            role.setId(1);
            user.setRole(role);
        }
        if (user.getProfilePicture() == null) {
            user.setProfilePicture("http://192.168.150.101//profile-picture/default.jpg");
        }
        LocalDateTime now = LocalDateTime.now();
        user.setCreateTime(now);
        user.setUpdateTime(now);
        userDao.insert(user);
        return user;
    }

    @Override
    public User update(User user) {
//        User oldUser = userDao.selectByUsername(user.getUsername());
        User oldUser = userDao.selectByUserId(user.getId());
        if (oldUser==null) {
            throw new BusinessException(ResultStatus.USER_NOT_FOUND);
        }
        String rawPassword = user.getPassword();
        if (rawPassword != null) {
            user.setPassword(passwordEncoder.encode(rawPassword));
        }
        user.setUpdateTime(LocalDateTime.now());
        userDao.update(user);
        return queryByUsername(user.getUsername());
    }

    @Override
    public boolean delete(String username) {
        return userDao.delete(username) > 0;
    }

}