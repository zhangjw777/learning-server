package com.learning.user.service;

import com.learning.user.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 *
 * @author 张家伟
 * @since 2025/04/04
 */
public interface RoleService {

    /**
     * 通过ID查询单个角色
     *
     * @param id 角色ID
     * @return 单个角色
     */
    Role queryById(Integer id);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> list();

    /**
     * 新增角色
     *
     * @param role 角色实例
     * @return 角色实例
     */
    Role create(Role role);

    /**
     * 更新角色
     *
     * @param role 角色实例
     * @return 角色实例
     */
    Role update(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否成功
     */
    boolean delete(Integer id);

}