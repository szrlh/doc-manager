package com.szr.docmanagerweb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 搜索结果 DTO
 * 包含匹配片段的核心信息，以及用于展示的内容片段
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResultDTO {

    /**
     * 片段ID
     */
    private Long sectionId;

    /**
     * 片段标题（可为空）
     */
    private String sectionTitle;

    /**
     * 内容摘要（截取匹配关键词附近文字）
     */
    private String contentSnippet;

    /**
     * 所属文档标题
     */
    private String documentTitle;

    /**
     * 分类名称（可为空）
     */
    private String categoryName;

    /**
     * 匹配相关度（标题匹配为2，正文匹配为1）
     */
    private Integer relevance;
}
