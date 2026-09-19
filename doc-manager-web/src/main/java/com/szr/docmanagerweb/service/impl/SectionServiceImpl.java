package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.szr.docmanagerweb.dto.SectionDTO;
import com.szr.docmanagerweb.entity.DocumentSection;
import com.szr.docmanagerweb.entity.SectionTag;
import com.szr.docmanagerweb.mapper.DocumentSectionMapper;
import com.szr.docmanagerweb.mapper.SectionTagMapper;
import com.szr.docmanagerweb.service.SectionService;
import com.szr.docmanagerweb.util.DateUtils;
import com.szr.docmanagerweb.util.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 文档片段服务实现类
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class SectionServiceImpl extends ServiceImpl<DocumentSectionMapper, DocumentSection> implements SectionService {

    private final DocumentSectionMapper sectionMapper;
    private final SectionTagMapper sectionTagMapper;

    @Override
    public IPage<SectionDTO> listSections(int page, int size, Long documentId, Long categoryId, Long tagId,
                                          String keyword) {
        // 创建分页参数（MyBatis-Plus 页码从1开始）
        Page<SectionDTO> pageParam = new Page<>(page + 1, size);
        // 执行自定义查询
        IPage<SectionDTO> sectionPage = sectionMapper.selectSectionPageWithDetails(pageParam, documentId, categoryId,
                tagId, keyword);
        List<SectionDTO> records = sectionPage.getRecords();
        // 处理 tagNames 字符串 -> List<String> + tagIdsStr 字符串 -> List<Long>
        records.forEach(this::processSectionTag);
        return sectionPage;
    }

    @Override
    public SectionDTO getSectionDetail(Long id) {
        SectionDTO dto = sectionMapper.selectSectionDetailById(id);
        if (dto == null) {
            throw new IllegalArgumentException("片段不存在: " + id);
        }
        processSectionTag(dto);
        return dto;
    }

    /**
     * 处理 tags 字符串 -> List<String> + tagIdsStr 字符串 -> List<Long>
     *
     * @param dto 片段 DTO
     */
    private void processSectionTag(SectionDTO dto) {
        // 处理 tags 字符串 -> List<String>
        if (dto.getTagNames() != null && !dto.getTagNames().isEmpty()) {
            dto.setTags(Arrays.asList(dto.getTagNames().split(",")));
        } else {
            dto.setTags(Collections.emptyList());
        }
        // 处理 tagIdsStr 字符串 -> List<Long>
        if (dto.getTagIdsStr() != null && !dto.getTagIdsStr().isEmpty()) {
            List<Long> ids = Arrays.stream(dto.getTagIdsStr().split(","))
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            dto.setTagIds(ids);
        } else {
            dto.setTagIds(Collections.emptyList());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSectionWithOriginalContent(Long sectionId, String newOriginalContent, String newTitle,
                                                 Long categoryId, List<Long> tagIds) {
        // 标题非空校验
        if (!StringUtils.hasText(newTitle)) {
            throw new IllegalArgumentException("标题不能为空");
        }
        // 内容非空校验
        if (!StringUtils.hasText(newOriginalContent)) {
            throw new IllegalArgumentException("内容不能为空");
        }
        
        DocumentSection section = this.getById(sectionId);
        if (section == null) {
            throw new IllegalArgumentException("片段不存在: " + sectionId);
        }

        // 生成纯文本（简化处理：去除 Markdown 标记）
        String plainText = MarkdownUtils.markdownToPlainText(newOriginalContent);

        // 使用 UpdateWrapper 显式更新，确保 category_id 可以置空
        UpdateWrapper<DocumentSection> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", sectionId)
                .set("title", newTitle)
                .set("original_content", newOriginalContent)
                .set("content", plainText)
                // 显式设置，包括null
                .set("category_id", categoryId)
                .set("update_time", DateUtils.now());
        
        this.update(updateWrapper);

        // 更新标签关联：先删除原有，再插入新的
        LambdaQueryWrapper<SectionTag> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SectionTag::getSectionId, sectionId);
        sectionTagMapper.delete(deleteWrapper);

        if (tagIds != null && !tagIds.isEmpty()) {
            for (Long tagId : tagIds) {
                SectionTag st = new SectionTag();
                st.setSectionId(sectionId);
                st.setTagId(tagId);
                sectionTagMapper.insert(st);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSection(Long sectionId) {
        DocumentSection section = this.getById(sectionId);
        if (section == null) {
            throw new IllegalArgumentException("片段不存在: " + sectionId);
        }

        // 删除关联标签
        LambdaQueryWrapper<SectionTag> tagWrapper = new LambdaQueryWrapper<>();
        tagWrapper.eq(SectionTag::getSectionId, sectionId);
        sectionTagMapper.delete(tagWrapper);

        // 删除片段
        this.removeById(sectionId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTags(Long sectionId, List<Long> tagIds) {
        if (this.getById(sectionId) == null) {
            throw new IllegalArgumentException("片段不存在: " + sectionId);
        }
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }

        // 查询已存在的关联
        LambdaQueryWrapper<SectionTag> existingWrapper = new LambdaQueryWrapper<>();
        existingWrapper.eq(SectionTag::getSectionId, sectionId)
                .in(SectionTag::getTagId, tagIds);
        List<SectionTag> existing = sectionTagMapper.selectList(existingWrapper);
        Set<Long> existingTagIds = existing.stream()
                .map(SectionTag::getTagId)
                .collect(Collectors.toSet());

        // 构建待插入列表
        List<SectionTag> toInsert = tagIds.stream()
                .filter(tagId -> !existingTagIds.contains(tagId))
                .map(tagId -> {
                    SectionTag st = new SectionTag();
                    st.setSectionId(sectionId);
                    st.setTagId(tagId);
                    return st;
                })
                .collect(Collectors.toList());

        // 调用批量插入
        if (!toInsert.isEmpty()) {
            sectionTagMapper.insertBatch(toInsert);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTag(Long sectionId, Long tagId) {
        LambdaQueryWrapper<SectionTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SectionTag::getSectionId, sectionId)
                .eq(SectionTag::getTagId, tagId);
        sectionTagMapper.delete(wrapper);
    }
}
