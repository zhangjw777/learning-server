package com.learning.course.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_course")
public class UserCourse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户-课程关系ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户名
     */
    @TableField("user_name")
    private String userName;

    /**
     * 课程ID
     */
    @TableField("course_id")
    private Long courseId;

    /**
     * 当前观看的章节Id
     */
    private Long currentChapter;

    /**
     * 用户是否已完成该课程 (1:是, 0:否)
     */
    @TableField("is_completed")
    private Integer isCompleted;

    /**
     * 课程完成时间
     */
    @TableField("completion_time")
    private LocalDateTime completionTime;


}
