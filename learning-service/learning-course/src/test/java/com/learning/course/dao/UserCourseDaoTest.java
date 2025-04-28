package com.learning.course.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserCourseDaoTest {
    @Autowired
    private final UserCourseDao userCourseDao;

    UserCourseDaoTest(UserCourseDao userCourseDao) {
        this.userCourseDao = userCourseDao;
    }


}