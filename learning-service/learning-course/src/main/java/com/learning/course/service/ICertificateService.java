package com.learning.course.service;

import com.github.pagehelper.PageInfo;
import com.learning.course.entity.Certificate;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户证书信息表 服务类
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
public interface ICertificateService extends IService<Certificate> {

    PageInfo<Certificate> listByUserName(int pageNum, int pageSize, String username);

    /**
     * 创建证书
     * @param userName
     * @param courseId
     */
    void createCertificate(String userName, Long courseId);
}
