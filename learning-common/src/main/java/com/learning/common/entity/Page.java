package com.learning.common.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 分页数据实体类
 *
 * @author 张家伟
 * @date 2025/04/07
 */
@Data
@ToString
@EqualsAndHashCode
public class Page<T> {

    /**
     * 数据列表
     */
    private List<T> list;
    /**
     * 数据总数
     */
    private Long size;

    public static <T> Page<T> of(List<T> list, Long size) {
        Page<T> page = new Page<>();
        page.setList(list);
        page.setSize(size);
        return page;
    }

}
