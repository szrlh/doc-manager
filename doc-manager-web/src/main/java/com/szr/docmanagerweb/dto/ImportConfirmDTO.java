package com.szr.docmanagerweb.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 导入确认 DTO
 * 用户在预览分类建议后，确认导入时提交的数据结构
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportConfirmDTO {
    
    /**
     * 文档标题（可修改）
     */
    private String documentTitle;

    /**
     * 文件类型（md/txt）
     */
    private String fileType;

    /**
     * 用户确认后的片段列表
     */
    private List<ImportedSectionConfirmDTO> sections;
}
