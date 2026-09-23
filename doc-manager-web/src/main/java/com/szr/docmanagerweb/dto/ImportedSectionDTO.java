package com.szr.docmanagerweb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导入时单个片段的信息
 * 包含原始内容、纯文本内容、标题、顺序及分类建议
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportedSectionDTO {

    /**
     * 片段标题（可为空）
     */
    private String title;

    /**
     * 原始 Markdown 内容
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
     * 推荐标签列表（来自 Markdown 父级标题）
     */
    private List<String> suggestedTags;

    /**
     * 分类建议列表，按置信度降序排列
     */
    private List<CategorySuggestionDTO> categorySuggestions;
}
