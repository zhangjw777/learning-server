package com.learning.trade.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 课程实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
@ToString
@EqualsAndHashCode
public class Course implements Serializable {

    private static final long serialVersionUID = -8737297239746489069L;
    /**
     * 课程ID
     */
    private Long id;
    /**
     * 名称
     */
    private String name;
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 积分价格
     */
    private Integer pointsPrice;
    /**
     * 是否报名
     */
    private Boolean registered;

}
