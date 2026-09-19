package com.szr.docmanagerweb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 标签实体类，对应表 tag
 * 存储标签信息，标签为扁平结构
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Data
@TableName("tag")
public class Tag {

    /**
     * 标签唯一ID，自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称，唯一约束防止重复
     */
    private String name;
}
