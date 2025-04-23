package com.learning.course.entity;

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
 * 用户证书信息表
 * </p>
 *
 * @author 张家伟
 * @since 2025-04-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("certificate")
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证书ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 获得证书的用户名
     */
    private String userName;

    /**
     * 关联的课程ID
     */
    private Long courseId;

    /**
     * 课程名称 (冗余方便查询)
     */
    private String courseName;

    /**
     * 证书图片存储链接 (例如 MinIO 的 URL)
     */
    private String certificateUrl;

    /**
     * 证书颁发日期
     */
    private LocalDateTime issueDate;

    /**
     * 记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 记录更新时间
     */
    private LocalDateTime updateTime;
}
