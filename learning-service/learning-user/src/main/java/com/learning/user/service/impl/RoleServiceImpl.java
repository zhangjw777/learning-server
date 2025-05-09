package com.learning.user.service.impl;

import com.learning.user.dao.RoleDao;
import com.learning.user.entity.Role;
import com.learning.user.service.RoleService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 角色服务实现类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Service
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Role queryById(Integer id) {
        return roleDao.selectById(id);
    }

    @Override
    public List<Role> list() {
        return roleDao.list();
    }

    @Override
    public Role create(Role role) {
        LocalDateTime now = LocalDateTime.now();
        role.setCreateTime(now);
        role.setUpdateTime(now);
        roleDao.insert(role);
        return role;
    }

    @Override
    public Role update(Role role) {
        role.setUpdateTime(LocalDateTime.now());
        roleDao.update(role);
        return roleDao.selectById(role.getId());
    }

    @Override
    public boolean delete(Integer id) {
        return roleDao.delete(id) > 0;
    }

}