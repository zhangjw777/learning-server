package com.learning.course.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 章节实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
@ToString
@EqualsAndHashCode
public class Chapter implements Serializable {

    private static final long serialVersionUID = -95094341782475066L;
    /**
     * 章节ID
     */
    private Long id;
    /**
     * 标题
     */
    private String title;
    /**
     * 内容类型
     */
    private String type;
    /**
     * 视频链接
     */
    private String videoUrl;
    /**
     * 视频时长
     */
    private String videoTime;
    /**
     * 文字内容
     */
    private String textContent;
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
