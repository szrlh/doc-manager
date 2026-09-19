package com.szr.docmanagerweb.dto;


import lombok.Data;

import java.util.List;

/**
 * 确认导入时单个片段的数据结构
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
public class ImportedSectionConfirmDTO {

    /**
     * 片段标题（可为空）
     */
    private String title;

    /**
     * 原始内容（Markdown 或纯文本）
     */
    private String originalContent;

    /**
     * 纯文本内容
     */
    private String content;

    /**
     * 片段顺序，从1开始
     */
    private Integer orderIndex;

    /**
     * 用户选择的分类ID（可空）
     */
    private Long categoryId;

    /**
     * 用户选择的标签ID列表（可空）
     */
    private List<Long> tagIds;
}
