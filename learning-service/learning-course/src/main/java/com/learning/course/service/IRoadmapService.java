package com.learning.course.service;

import com.learning.course.entity.Roadmap;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 张家伟
 * @since 2025-05-06
 */
public interface IRoadmapService extends IService<Roadmap> {

    List<Roadmap> listByCategoryId(Integer categoryId);
}
