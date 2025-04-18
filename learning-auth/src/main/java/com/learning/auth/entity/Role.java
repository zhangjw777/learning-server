package com.learning.auth.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 角色实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
public class Role implements Serializable {

    private static final long serialVersionUID = -44062214609899076L;
    /**
     * 角色名
     */
    private String name;


}