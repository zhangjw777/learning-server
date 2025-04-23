package com.learning.course.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.PageInfo;
import com.learning.course.entity.Certificate;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户证书信息表 Mapper 接口
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
@Mapper
public interface CertificateMapper extends BaseMapper<Certificate> {

    List<Certificate> listByUserName(String username);
}
