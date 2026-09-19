package com.szr.docmanagerweb.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.szr.docmanagerweb.dto.SectionDTO;
import com.szr.docmanagerweb.entity.DocumentSection;

import java.util.List;

/**
 * 文档片段服务接口
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface SectionService extends IService<DocumentSection> {

    /**
     * 分页查询片段
     * @param page 页码
     * @param size 每页大小
     * @param documentId 文档ID
     * @param categoryId 分类ID
     * @param tagId 标签ID
     * @param keyword 关键词
     * @return 片段分页数据
     */
    IPage<SectionDTO> listSections(int page, int size, Long documentId, Long categoryId, Long tagId, String keyword);

    /**
     * 获取片段详情
     * @param id 片段ID
     * @return 片段详情
     */
    SectionDTO getSectionDetail(Long id);

    /**
     * 更新片段（重新生成纯文本 content）
     * @param sectionId 片段ID
     * @param newOriginalContent 新的原始内容
     * @param newTitle 新的标题
     * @param categoryId 分类ID
     * @param tagIds 标签ID
     */
    void updateSectionWithOriginalContent(Long sectionId, String newOriginalContent, String newTitle, Long categoryId,
                                          List<Long> tagIds);

    /**
     * 删除片段
     * @param sectionId 片段ID
     */
    void deleteSection(Long sectionId);

    /**
     * 为片段添加标签
     * @param sectionId 片段ID
     * @param tagIds 标签ID列表
     */
    void addTags(Long sectionId, List<Long> tagIds);

    /**
     * 移除片段标签
     * @param sectionId 片段ID
     * @param tagId 标签ID
     */
    void removeTag(Long sectionId, Long tagId);
}
