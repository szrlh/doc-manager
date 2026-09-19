package com.szr.docmanagerweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szr.docmanagerweb.dto.SectionDTO;
import com.szr.docmanagerweb.entity.DocumentSection;
import org.apache.ibatis.annotations.Param;

/**
 * 文档片段 Mapper 接口
 * 提供片段表的基础操作及自定义关联查询
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface DocumentSectionMapper extends BaseMapper<DocumentSection> {

    /**
     * 分页查询片段列表，并关联文档标题、分类名称、标签列表
     * 使用多个 LEFT JOIN 获取关联信息，并通过子查询或 GROUP_CONCAT 聚合标签
     *
     * @param page        分页参数
     * @param documentId  文档ID（可空）
     * @param categoryId  分类ID（可空）
     * @param tagId       标签ID（可空）
     * @param keyword     关键词（可空，匹配标题或纯文本内容）
     * @return 分页的 SectionDTO 列表
     */
    IPage<SectionDTO> selectSectionPageWithDetails(IPage<SectionDTO> page,
                                                   @Param("documentId") Long documentId,
                                                   @Param("categoryId") Long categoryId,
                                                   @Param("tagId") Long tagId,
                                                   @Param("keyword") String keyword);

    /**
     * 根据片段ID获取详情（包含关联信息）
     * @param sectionId 片段ID
     * @return SectionDTO
     */
    SectionDTO selectSectionDetailById(@Param("sectionId") Long sectionId);
}
