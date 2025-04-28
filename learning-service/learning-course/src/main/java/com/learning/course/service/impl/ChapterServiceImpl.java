package com.learning.course.service.impl;

import com.learning.course.dao.ChapterDao;
import com.learning.course.entity.Chapter;
import com.learning.course.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 章节服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Service
@RequiredArgsConstructor
public class ChapterServiceImpl implements ChapterService {

    private final ChapterDao chapterDao;

    @Override
    public Chapter queryById(Long id) {
        return chapterDao.selectById(id);
    }

    @Override
    public List<Chapter> list() {
        return chapterDao.list();
    }

    @Override
    public List<Chapter> listByCourseId(Long courseId) {
        return chapterDao.listByCourseId(courseId);
    }

    @Override
    public List<Chapter> listInfoByCourseId(Long courseId) {
        return chapterDao.listInfoByCourseId(courseId);
    }


    @Override
    public Chapter create(Chapter chapter) {
        String videoTime = chapter.getVideoTime();
        if (videoTime != null) {
            chapter.setVideoTime(transformVideoTime(videoTime));
        }
        LocalDateTime now = LocalDateTime.now();
        chapter.setCreateTime(now);
        chapter.setUpdateTime(now);
        chapterDao.insert(chapter);
        return chapter;
    }

    @Override
    public Chapter update(Chapter chapter) {
        String videoTime = chapter.getVideoTime();
        if (videoTime != null) {
            chapter.setVideoTime(transformVideoTime(videoTime));
        }
        chapter.setUpdateTime(LocalDateTime.now());
        chapterDao.update(chapter);
        return chapterDao.selectById(chapter.getId());
    }

    @Override
    public boolean delete(Long id) {
        return chapterDao.delete(id) > 0;
    }


    private String transformVideoTime(String videoTime) {
        double duration;
        try {
            duration = Double.parseDouble(videoTime);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("参数异常");
        }
        long minutes = (long) (duration / 60);
        long seconds = (long) (duration % 60);
        return (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }

}