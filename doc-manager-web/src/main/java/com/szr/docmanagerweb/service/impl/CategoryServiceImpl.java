package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.szr.docmanagerweb.entity.Category;
import com.szr.docmanagerweb.mapper.CategoryMapper;
import com.szr.docmanagerweb.service.CategoryService;
import com.szr.docmanagerweb.util.DateUtils;
import com.szr.docmanagerweb.vo.CategoryTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<CategoryTreeVO> getCategoryTree() {
        // 查询所有分类
        List<Category> categories = this.list();

        // 转换为 VO 列表
        List<CategoryTreeVO> voList = categories.stream().map(category -> {
            CategoryTreeVO vo = new CategoryTreeVO();
            vo.setId(category.getId());
            vo.setName(category.getName());
            return vo;
        }).toList();

        // 构建父子关系映射
        Map<Long, CategoryTreeVO> voMap = voList.stream()
                .collect(Collectors.toMap(CategoryTreeVO::getId, vo -> vo));

        List<CategoryTreeVO> tree = new ArrayList<>();
        for (CategoryTreeVO vo : voList) {
            Long parentId = categories.stream()
                    .filter(c -> c.getId().equals(vo.getId()))
                    .findFirst()
                    .map(Category::getParentId)
                    .orElse(null);
            if (parentId == null) {
                tree.add(vo);
            } else {
                CategoryTreeVO parent = voMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(vo);
                } else {
                    // 父分类不存在，作为顶级处理
                    tree.add(vo);
                }
            }
        }
        return tree;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Category saveCategory(Category category) {
        // 校验名称
        if (!StringUtils.hasText(category.getName())) {
            throw new IllegalArgumentException("分类名称不能为空");
        }

        // 校验父分类
        if (category.getParentId() != null) {
            Category parent = this.getById(category.getParentId());
            if (parent == null) {
                throw new IllegalArgumentException("父分类不存在: " + category.getParentId());
            }
            // 防止循环引用（简单处理：不允许将自身或子分类设置为父分类，这里仅检查自身）
            if (category.getId() != null && category.getId().equals(category.getParentId())) {
                throw new IllegalArgumentException("不能将自身设置为父分类");
            }
        }
        
        String timeStr = DateUtils.now();

        if (category.getId() == null) {
            // 新增
            category.setCreateTime(timeStr);
            category.setUpdateTime(timeStr);
            this.save(category);
        } else {
            // 更新
            category.setUpdateTime(timeStr);
            this.updateById(category);
        }
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long categoryId) {
        Category category = this.getById(categoryId);
        if (category == null) {
            throw new IllegalArgumentException("分类不存在: " + categoryId);
        }

        // 检查子分类
        LambdaQueryWrapper<Category> childQuery = new LambdaQueryWrapper<>();
        childQuery.eq(Category::getParentId, categoryId);
        long childCount = this.count(childQuery);
        if (childCount > 0) {
            throw new IllegalStateException("存在子分类，无法删除");
        }

        // 删除分类
        this.removeById(categoryId);
    }
}
