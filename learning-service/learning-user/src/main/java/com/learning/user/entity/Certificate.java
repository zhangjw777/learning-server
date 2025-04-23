package com.learning.user.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
public class Certificate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 证书ID
     */
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
