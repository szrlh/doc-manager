package com.szr.docmanagerweb.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.szr.docmanagerweb.entity.Category;
import com.szr.docmanagerweb.vo.CategoryTreeVO;

import java.util.List;

/**
 * 分类服务接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface CategoryService extends IService<Category> {

    /**
     * 获取分类树
     * @return 分类树形结构列表
     */
    List<CategoryTreeVO> getCategoryTree();

    /**
     * 保存分类（新增或更新）
     * @param category 分类实体
     * @return 保存后的分类
     */
    Category saveCategory(Category category);

    /**
     * 删除分类（处理关联片段）
     * @param categoryId 分类ID
     */
    void deleteCategory(Long categoryId);
}
