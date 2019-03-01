package com.github.everything.core.model;

import lombok.Data;

/**
 * 文件检索条件
 */
@Data
public class Condition {
    private String name;

    private String fileType;

    private Integer limit;

    /**
     * 检索结果的文件信息的depth排序规则
     * 1、默认是TRUE ->asc
     * 2、false-->desc
     */
    private Boolean orderByArc;
}
