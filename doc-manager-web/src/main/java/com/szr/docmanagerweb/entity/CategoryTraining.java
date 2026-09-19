package com.szr.docmanagerweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 分类训练数据实体类，对应表 category_training
 * 存储用户确认的片段分类样本，用于训练智能分类模型
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@TableName("category_training")
public class CategoryTraining {

    /**
     * 训练样本唯一ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 片段纯文本内容（截取前500字符）
     */
    private String contentText;

    /**
     * 用户最终确认的分类ID，关联 category 表
     */
    private Long categoryId;

    /**
     * 样本创建时间，格式为本地时间字符串
     */
    private String createTime;
}
