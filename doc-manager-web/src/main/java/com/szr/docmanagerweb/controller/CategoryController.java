package com.szr.docmanagerweb.controller;


import com.szr.docmanagerweb.entity.Category;
import com.szr.docmanagerweb.service.CategoryService;
import com.szr.docmanagerweb.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     *
     * @return 分类树形结构列表
     */
    @GetMapping("/tree")
    public List<CategoryTreeVO> getTree() {
        return categoryService.getCategoryTree();
    }

    /**
     * 获取所有分类（扁平列表）
     *
     * @return 分类列表
     */
    @GetMapping
    public List<Category> list() {
        return categoryService.list();
    }

    /**
     * 创建分类
     *
     * @param category 分类实体（不含ID）
     * @return 创建后的分类（含ID）
     */
    @PostMapping
    public Category create(@RequestBody Category category) {
        return categoryService.saveCategory(category);
    }

    /**
     * 更新分类
     *
     * @param id       分类ID
     * @param category 分类实体
     * @return 更新后的分类
     */
    @PutMapping("/{id}")
    public Category update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return categoryService.saveCategory(category);
    }

    /**
     * 删除分类（处理关联片段）
     *
     * @param id 分类ID
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
