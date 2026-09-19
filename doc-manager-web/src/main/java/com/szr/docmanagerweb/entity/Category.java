package com.szr.docmanagerweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 分类实体类，对应表 category
 * 用于存储文档片段的分类信息，支持树形层级结构
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@TableName("category")
public class Category {
    
    /**
     * 分类唯一ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称（如“技术/编程”）
     */
    private String name;

    /**
     * 分类描述，可为空
     */
    private String description;

    /**
     * 父分类ID，用于构建树形结构，NULL表示顶级分类
     */
    private Long parentId;

    /**
     * 规则匹配关键词（JSON数组），用于快速分类
     */
    private String ruleKeywords;

    /**
     * 创建时间，格式为本地时间字符串（如 2024-01-01 12:00:00）
     */
    private String createTime;

    /**
     * 最后更新时间，格式同上
     */
    private String updateTime;
}
