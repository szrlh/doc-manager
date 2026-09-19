package com.szr.docmanagerweb.vo;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类树形结构 VO
 * 用于前端展示分类选择器或管理页面
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
public class CategoryTreeVO {

    /**
     * 分类ID
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 子分类列表
     */
    private List<CategoryTreeVO> children = new ArrayList<>();
}
