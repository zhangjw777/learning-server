package com.learning.course.service;

import java.util.List;
import java.util.Map;

public interface IStatistic {
    List<Map<String, Object>> statisticCourseHotness();

    List<Map<String, Object>> statisticCourseFinishRate();

    List<Map<String, Object>> statisticUserActivity();

    List<Map<String, Object>> statisticPointsSourceRate();

    List<Map<String, Object>> statisticCourseCategoryDistribute();

    List<Map<String, Object>> statisticPointsRank();

    List<Map<String, Object>> statisticTeacherMatrix();
}
