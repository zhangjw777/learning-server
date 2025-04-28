package com.learning.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.course.dao.CourseDao;
import com.learning.course.dao.UserCourseDao;
import com.learning.course.entity.Course;
import com.learning.course.entity.UserCourse;
import com.learning.course.service.IStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticImpl implements IStatistic {
    private final CourseDao courseDao;
    private final CourseViewsServiceImpl courseViewsService;
    private final UserCourseService userCourseService;
    private final UserCourseDao userCourseDao;

    @Override
    public List<Map<String, Object>> statisticCourseHotness() {
        List<Course> courses = courseDao.list(true, "create_time");

        //获得课程用户数量的Map对象
        List<Map<String, Long>> result = userCourseService.statisticUserCourse();
        log.info("result: {}", result.toString());
        Map<Long, Long> courseUserCountMap = new HashMap<>();//课程id:用户数量
        for (Map<String, Long> item : result) {
            courseUserCountMap.put(item.get("courseId"), item.get("userCount"));
        }
        //将map对象以及课程访问次数进行合并计算,注意不是每个课程都有学生学习，但每个课程都有点击量
        Map<String, Integer> hotnessData = new HashMap<>();
        for (Course course : courses) {
            int views = courseViewsService.queryViewCountsOfCourse(course);
            int count = 0;
            //判断course.id是否在result中的键courseId中能找到
            if (courseUserCountMap.containsKey(course.getId())) {
                count = (int) (courseUserCountMap.get(course.getId()) * 10);
            }
            hotnessData.put(course.getName(), views + count);
        }
        List<Map<String, Object>> hotnessDataList = new ArrayList<>();
        hotnessData.forEach((k, v) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("name", k);
            map.put("hotness", v);
            hotnessDataList.add(map);
        });

        return hotnessDataList;
    }

    @Override
    public List<Map<String, Object>> statisticCourseFinishRate() {
        List<Map<String, Object>> finishRateList = new ArrayList<>();
        List<Course> courses = courseDao.list(true, "create_time");
        // 将课程列表转换为 Map<courseId, courseName> 以便快速查找
        Map<Long, String> courseIdToNameMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getName));
        List<Map<String, Object>> courseCompletionStats = userCourseDao.getCourseCompletionStats();
        log.info("courseCompletionStats: {}", courseCompletionStats.toString());
        // 遍历每个课程的完成情况，将其封装成结果Map并加入到列表中
        for (Map<String, Object> courseCompletionStat : courseCompletionStats) {
            Long courseId = (Long) courseCompletionStat.get("course_id");
            String courseName = courseIdToNameMap.get(courseId);
            Long totalLearners = (Long) courseCompletionStat.get("total_learners");
            BigDecimal completedLearners = (BigDecimal) courseCompletionStat.get("completed_learners");
            float completionRate = (completedLearners.floatValue() / totalLearners.floatValue()) * 100;
            Map<String, Object> map = new HashMap<>();
            map.put("name", courseName);
            map.put("rate",  Float.valueOf(completionRate).intValue());
            finishRateList.add(map);
        }
        //未完待续
        return finishRateList;
    }
}
