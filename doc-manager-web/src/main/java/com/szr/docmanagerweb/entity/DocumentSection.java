package com.szr.docmanagerweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文档片段实体类，对应表 document_section
 * 存储从文档中拆分的每个独立片段，是核心业务实体
 * original_content 保存原始 Markdown 内容（或纯文本），
 * content 保存去除 Markdown 标记后的纯文本，用于搜索和分类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@TableName("document_section")
public class DocumentSection {
    
    /**
     * 片段唯一ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属文档ID，关联 document 表
     */
    private Long documentId;

    /**
     * 片段标题（纯文本，可为空）
     */
    private String title;

    /**
     * 原始内容（保留 Markdown 格式，非空）
     */
    private String originalContent;

    /**
     * 纯文本内容（用于搜索、分类，非空）
     */
    private String content;

    /**
     * 片段在文档中的顺序，从1开始递增
     */
    private Integer orderIndex;

    /**
     * 片段独立分类ID（可空，表示未分类）
     */
    private Long categoryId;

    /**
     * 创建时间，格式为本地时间字符串
     */
    private String createTime;

    /**
     * 最后更新时间，格式同上
     */
    private String updateTime;
}
