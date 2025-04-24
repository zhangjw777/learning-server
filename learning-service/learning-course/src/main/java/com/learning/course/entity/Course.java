package com.learning.course.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类
 *
 * @author 张家伟
 * @since 2025/04/04
 */
@Data
@ToString
@EqualsAndHashCode
@Document(indexName = "courses")
@TableName("course")
public class Course implements Serializable {

    private static final long serialVersionUID = -38543796891982620L;
    /**
     * 课程ID
     */
    @Id
    private Long id;
    /**
     * 名称
     */
    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String name;
    /**
     * 价格
     */
    @Field(type = FieldType.Text)
    private BigDecimal price;

    /**
     * 积分价格
     */

    private Integer pointsPrice;
    /**
     * 简介
     */
    @Field(analyzer = "ik_max_word", type = FieldType.Text)
    private String description;
    /**
     * 是否报名
     */
    @Transient
    private Boolean registered;
    /**
     * 教师ID
     */
    @Transient
    private User teacher;
    /**
     * 平均评分
     */
    @Transient
    private Integer averageScore;
    /**
     * 封面图片地址
     */
    @Field(name = "cover_picture", type = FieldType.Text)
    private String coverPicture;
    /**
     * 审核通过
     */
    @Transient
    private Boolean approved;
    @Transient
    private Integer chapterCount;
    @Transient
    private Integer pointsReward;
    @Transient
    private String certificateUrl;
    @Transient
    private Integer isPremium;
    /**
     * 创建时间
     */
    @Transient
    private LocalDateTime createTime;
    /**
     * 修改时间
     */
    @Transient
    private LocalDateTime updateTime;
    /**
     * 分类id
     */
    @Transient
    private Integer categoryId;
    @Transient
    private Integer viewCounts;

    //@Transient是用于spring data来忽略字段，这里针对es,对于mybatis无效
}
