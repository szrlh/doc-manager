package com.szr.docmanagerweb.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * 片段详情/列表项 DTO
 * 包含片段核心信息，以及所属文档、分类、标签等关联信息
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
public class SectionDTO {

    /**
     * 片段ID
     */
    private Long id;

    /**
     * 所属文档ID
     */
    private Long documentId;

    /**
     * 所属文档标题
     */
    private String documentTitle;

    /**
     * 所属文档文件类型（md/txt），用于前端判断渲染方式
     */
    private String fileType;

    /**
     * 片段标题（纯文本）
     */
    private String title;

    /**
     * 纯文本内容（用于摘要展示）
     */
    private String content;

    /**
     * 原始 Markdown 内容（详情或编辑时使用）
     */
    private String originalContent;

    /**
     * 片段在文档中的顺序
     */
    private Integer orderIndex;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 标签名称列表
     */
    private List<String> tags;

    /**
     * 标签id列表
     */
    private List<Long> tagIds;

    /**
     * 数据库聚合的原始标签名称字符串，不直接暴露给前端
     */
    @JsonIgnore
    private String tagNames;

    /**
     * 数据库聚合的原始标签id字符串，不直接暴露给前端
     */
    @JsonIgnore
    private String tagIdsStr;

    /**
     * 最后更新时间
     */
    private String updateTime;
}
