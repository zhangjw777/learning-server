package com.learning.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.course.client.UserClient;
import com.learning.course.dao.*;
import com.learning.course.entity.*;
import com.learning.course.service.IStatistic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    private final EvaluationDao evaluationDao;
    private final NoteDao noteDao;
    private final QuestionDao questionDao;
    private final AnswerDao answerDao;
    private final CategoryDao categoryDao;
    private final UserClient userClient;

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
        return finishRateList;
    }

    @Override
    public List<Map<String, Object>> statisticUserActivity() {
        // 用户活跃度：课程评价人数*5+笔记发布个数*6+问题个数*5+回答个数*7+购买课程人数*10 *常数
        List<Map<String, Object>> result = new ArrayList<>(); // 保存返回结果
        int constant = 8; // 常数控制

        // 获取各项统计数据
        List<Map<String, Object>> evaluationCountOf7Days = evaluationDao.statisticCountOf7Days();
        List<Map<String, Object>> noteCountOf7Days = noteDao.statisticCountOf7Days();
        List<Map<String, Object>> questionCountOf7Days = questionDao.statisticCountOf7Days();
        List<Map<String, Object>> answerCountOf7Days = answerDao.statisticCountOf7Days();
        List<Map<String, Object>> userCourseCountOf7Days = userCourseDao.statisticCountOf7Days();

        // 确保userCourseCountOf7Days有数据，因为我们用它的日期作为基准
        if (userCourseCountOf7Days == null || userCourseCountOf7Days.size() < 7) {
            // 如果基准数据不足，可以返回空结果或进行其他处理
            return result;
        }

        for (int i = 0; i < 7; i++) { // 0-6代表7天中的第一天到第七天
            String date = (String) evaluationCountOf7Days.get(i).get("user_course_date");
            long activityDegree = 0;

            // 计算课程评价的活跃度（乘以5）
            activityDegree += getCountForDay(evaluationCountOf7Days, i, "count") * 5;

            // 计算笔记发布的活跃度（乘以6）
            activityDegree += getCountForDay(noteCountOf7Days, i, "count") * 6;

            // 计算问题的活跃度（乘以5）
            activityDegree += getCountForDay(questionCountOf7Days, i, "count") * 5;

            // 计算回答的活跃度（乘以7）
            activityDegree += getCountForDay(answerCountOf7Days, i, "count") * 7;

            // 计算购买课程的活跃度（乘以10）
            activityDegree += getCountForDay(userCourseCountOf7Days, i, "count") * 10;

            // 乘以常数
            activityDegree *= constant;

            Map<String, Object> map = new HashMap<>();
            map.put("date", date);
            map.put("totalActivityScore", activityDegree);
            result.add(map);
        }
         return result;
    }

    /**
     * 统计用户获取积分来源分布渠道占比
     * @return
     */
    @Override
    public List<Map<String, Object>> statisticPointsSourceRate() {
        // 积分获取渠道占比：课程奖励积分 = SUM(课程完成的points_reward)，
        // 回答奖励积分 = COUNT(有效回答)*单次奖励值，
        // COUNT(有效笔记)*发布笔记获得积分
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Course> courses = courseDao.list(true, "create_time");
        // 将课程列表转换为 Map<courseId, pointsReward> 以便快速查找
        Map<Long, Integer> courseIdToRewardsMap = courses.stream()
                .collect(Collectors.toMap(Course::getId, Course::getPointsReward));
        // 遍历课程完成状态，对课程奖励积分求和
        List<Map<String, Object>> completionStats = userCourseDao.getCourseCompletionStats();
        int totalCompletedPoints = 0;
        for (Map<String, Object> completionStat : completionStats) {
            BigDecimal completedLearners = (BigDecimal) completionStat.get("completed_learners");
            if (completedLearners.intValue()<0) //说明该课程没有用户完成，则跳过该课程的统计
                continue;
            Long courseId = (Long) completionStat.get("course_id");
            Integer pointReward = courseIdToRewardsMap.get(courseId);
            totalCompletedPoints += pointReward * completedLearners.intValue();
        }
        resultList.add(Map.of("name", "完成课程奖励", "value", totalCompletedPoints));
        // 回答奖励积分
        Integer answerCount = answerDao.count();
        resultList.add(Map.of("name", "回答问题奖励", "value", answerCount * 20));
        //发布笔记积分
        Integer noteCount = noteDao.count();
        resultList.add(Map.of("name", "发布笔记奖励", "value", noteCount * 10));
        return resultList;
    }

    @Override
    public List<Map<String, Object>> statisticCourseCategoryDistribute() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        //创建分类id与名字的映射 id:name
        List<Category> categories = categoryDao.list();
        Map<Integer, String> catIdToNameMap = categories.stream().collect(Collectors.toMap(Category::getId, Category::getName));
        List<Map<String, Object>> courseOfCategoryCounts = courseDao.statisticCourseOfCategoryCounts();
        for (Map<String, Object> item : courseOfCategoryCounts){
            Integer categoryId = (Integer) item.get("category_id");
            Long value = (Long) item.get("count");
            Map<String, Object> map = new HashMap<>();
            map.put("name", catIdToNameMap.get(categoryId));
            map.put("value", value.intValue());
            resultList.add(map);
        }
        return resultList;
    }

    @Override
    public List<Map<String, Object>> statisticPointsRank() {
        List<Map<String, Object>> pointsMapList = userClient.getPointsMap().getData();
        return pointsMapList;
    }

    @Override
    public List<Map<String, Object>> statisticTeacherMatrix() {
//        {
//            name: "张老师", // 教师用户名或姓名
//            // 指标值：平均完播率(%), 每课程平均问题数, 课程平均热度
//            value: [85, 15, 350],
//        }
        List<Map<String, Object>> resultList= new ArrayList<>();
        List<Question> questions = questionDao.list();
        List<Course> courses = courseDao.list(true, "create_time");
        List<User> teachers = userClient.listUserByRole("教师").getData();
        Map<String, List<Course>> teacherCourseMap= new HashMap<>();
        // 遍历课程列表，将每个课程分配给对应的教师,构建teacherCourseMap teacherName:courseList
        for (User teacher : teachers) {
            List<Course> courseList = new ArrayList<>();
            for (Course course : courses){
                if (course.getTeacher().getUsername().equals(teacher.getUsername())){
                    if (!teacherCourseMap.containsKey(teacher.getUsername())){
                        teacherCourseMap.put(teacher.getUsername(),courseList );
                    }
                    teacherCourseMap.get(teacher.getUsername()).add(course);//把课程添加到教师对应的课程列表中
                }
            }
        }
        //计算每个老师课程中的课程平均完成度
        List<Map<String, Object>> coursesFinishRate = this.statisticCourseFinishRate(); //[{name:value rate:value}]
        List<Map<String, Object>> courseHotnessList = this.statisticCourseHotness();// [{name:value,hotness:value}]
        teacherCourseMap.forEach((teacherName, courseList) -> {
            //获取课程完播率 课程问题平均数

            float averageFinishRate = 0;
            float averageQuestionCount = 0;
            float averageHotness = 0;
            for (Course course : courseList) {
                //从coursesFinishRate中找到是否存在course,判断条件 课程名字相同
                Map<String, Object> finishRateMap = coursesFinishRate.stream()
                        .filter(item -> item.get("name").equals(course.getName()))
                        .findFirst()
                        .orElse(null);
                if (finishRateMap != null) {
                    averageFinishRate += ((Integer) finishRateMap.get("rate")).floatValue();
                }
                //从questions中查找有没有该课程，并统计数量
                long questionCount = questions.stream()
                        .filter(question -> question.getCourseId().equals(course.getId()))
                        .count();
                if (questionCount > 0){
                    averageQuestionCount+= questionCount;
                }
                //从courseHotnessList中查找有没有该课程，并统计热度
                Map<String, Object> hotnessMap = courseHotnessList.stream()
                        .filter(item -> item.get("name").equals(course.getName()))
                        .findFirst()
                        .orElse(null);
                if (hotnessMap != null)
                    averageHotness  +=((Integer) hotnessMap.get("hotness")).floatValue();

            }
            //将三者进行处理后，封装为List
            averageFinishRate /= courseList.size();
            averageQuestionCount  /= courseList.size();
            averageHotness /= courseList.size();
            Map<String, Object> map = new HashMap<>();
            map.put("name", teacherName);
            List <Integer> value= new ArrayList<>();
            value.add((int) (averageFinishRate * 100));
            value.add((int) averageQuestionCount);
            value.add((int) averageHotness);
            map.put("value",value );
            resultList.add(map);
        });



        return resultList;
    }

    /**
     * 辅助方法 安全获取指定日期的计数值，避免索引越界
     * @param dataList 数据列表
     * @param index 索引
     * @param countKey 计数键名
     * @return 计数值，如果无法获取则返回0
     */
    private long getCountForDay(List<Map<String, Object>> dataList, int index, String countKey) {
        if (dataList == null || index >= dataList.size()) {
            return 0; // 数据不存在或索引越界，返回0
        }

        Object countObj = dataList.get(index).get(countKey);
        if (countObj instanceof Long) {
            return (Long) countObj;
        } else if (countObj instanceof Integer) {
            return ((Integer) countObj).longValue();
        } else if (countObj instanceof Number) {
            return ((Number) countObj).longValue();
        }

        return 0; // 数据类型不匹配，返回0
    }
}
