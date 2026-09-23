package com.szr.docmanagerweb.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 片段-标签关联实体类，对应表 section_tag
 * 多对多关系：一个片段可有多个标签，一个标签可属于多个片段
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("section_tag")
public class SectionTag {

    /**
     * 自增主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 片段ID（联合主键之一）
     */
    private Long sectionId;

    /**
     * 标签ID（联合主键之一）
     */
    private Long tagId;
}
