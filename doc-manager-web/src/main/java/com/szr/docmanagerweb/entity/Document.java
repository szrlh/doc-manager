package com.szr.docmanagerweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 文档实体类，对应表 document
 * 存储导入的原始文档元数据，不包含具体内容（内容拆分为片段）
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@TableName("document")
public class Document {
    
    /**
     * 文档唯一ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档标题（文件名或首个标题）
     */
    private String title;

    /**
     * 源文件类型，限定为 'md' 或 'txt'
     */
    private String fileType;

    /**
     * 导入时间，格式为本地时间字符串
     */
    private String createTime;

    /**
     * 最后修改时间，格式同上
     */
    private String updateTime;
}
