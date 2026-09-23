package com.szr.docmanagerweb.dto;


import lombok.*;

/**
 * 分类建议 DTO
 * 包含分类ID、名称和置信度
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategorySuggestionDTO {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 置信度（0~1之间）
     */
    private Double confidence;
}
