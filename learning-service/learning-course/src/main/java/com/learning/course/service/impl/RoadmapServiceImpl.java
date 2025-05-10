package com.learning.course.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.learning.course.entity.Roadmap;
import com.learning.course.dao.RoadmapMapper;
import com.learning.course.service.IRoadmapService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 张家伟
 * @since 2025-05-06
 */
@RequiredArgsConstructor
@Service
public class RoadmapServiceImpl extends ServiceImpl<RoadmapMapper, Roadmap> implements IRoadmapService {
    private final RoadmapMapper roadmapMapper;

    @Override
    public List<Roadmap> listByCategoryId(Integer categoryId) {
        return roadmapMapper.selectList(new LambdaQueryWrapper<Roadmap>().eq(Roadmap::getCategoryId, categoryId));
    }
}
