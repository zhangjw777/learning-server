package com.learning.course.service;

import java.util.List;
import java.util.Map;

public interface IStatistic {
    List<Map<String, Object>> statisticCourseHotness();

    List<Map<String, Object>> statisticCourseFinishRate();
}
