package com.szr.docmanagerweb.dto;


import lombok.Data;

/**
 * 文档列表项 DTO
 * 包含文档元数据及片段数量，用于文档列表展示
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
public class DocumentDTO {

    /**
     * 文档ID
     */
    private Long id;

    /**
     * 文档标题
     */
    private String title;

    /**
     * 文件类型（md/txt）
     */
    private String fileType;

    /**
     * 文档下的片段数量
     */
    private Integer sectionCount;

    /**
     * 导入时间
     */
    private String createTime;

    /**
     * 最后修改时间
     */
    private String updateTime;
}
