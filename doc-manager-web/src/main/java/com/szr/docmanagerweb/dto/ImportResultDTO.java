package com.szr.docmanagerweb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导入结果 DTO
 * 用于在导入文档时，返回解析后的片段列表及每个片段的分类建议
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {

    /**
     * 文档标题（文件名或首个标题）
     */
    private String documentTitle;

    /**
     * 文件类型（md/txt）
     */
    private String fileType;

    /**
     * 片段列表
     */
    private List<ImportedSectionDTO> sections;
}
