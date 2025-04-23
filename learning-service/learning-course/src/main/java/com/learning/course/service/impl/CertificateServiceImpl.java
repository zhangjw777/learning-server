package com.learning.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.learning.course.dao.CourseDao;
import com.learning.course.entity.Certificate;
import com.learning.course.dao.CertificateMapper;
import com.learning.course.entity.Course;
import com.learning.course.service.CourseService;
import com.learning.course.service.ICertificateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 用户证书信息表 服务实现类
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements ICertificateService {

    private final CertificateMapper certificateMapper;
    private final CourseDao courseDao;
    private final CourseService courseService;

    @Override
    public PageInfo<Certificate> listByUserName(int pageNum, int pageSize, String username) {
        PageHelper.startPage(pageNum, pageSize);
        return PageInfo.of(certificateMapper.listByUserName(username));
    }

    @Override
    public void createCertificate(String userName, Long courseId) {
        Course course = courseDao.selectById(courseId);
        if (course.getIsPremium()!=1){
            log.info("非精品课程，无证书创建");
            return;
        }else {
            Certificate certificate = new Certificate();
            certificate.setUserName(userName);
            certificate.setCourseId(courseId);
            certificate.setCourseName(course.getName());
            String certificateUrl = course.getCertificateUrl();
            certificate.setCertificateUrl(certificateUrl);
            certificate.setIssueDate(LocalDateTime.now());
            certificateMapper.insert(certificate);
        }
    }
}
