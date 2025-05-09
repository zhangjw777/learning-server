package com.learning.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
@ToString
@EqualsAndHashCode
public class Category implements Serializable {

    private static final long serialVersionUID = 586009742825901403L;
    /**
     * 分类ID
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 父级ID
     */
    private Integer parentId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
