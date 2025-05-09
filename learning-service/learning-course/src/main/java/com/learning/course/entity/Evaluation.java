package com.learning.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 评价实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
@ToString
@EqualsAndHashCode
public class Evaluation implements Serializable {

    private static final long serialVersionUID = -84422415880387160L;
    /**
     * 评价ID
     */
    private Long id;
    /**
     * 评分
     */
    private Integer score;
    /**
     * 评论
     */
    private String comment;
    /**
     * 用户名
     */
    private User author;
    /**
     * 课程ID
     */
    private Long courseId;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Transient
    private String courseName;
}
