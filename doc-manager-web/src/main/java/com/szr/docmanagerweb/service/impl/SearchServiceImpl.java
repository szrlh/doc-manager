package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.docmanagerweb.dto.SearchResultDTO;
import com.szr.docmanagerweb.entity.Category;
import com.szr.docmanagerweb.entity.Document;
import com.szr.docmanagerweb.entity.DocumentSection;
import com.szr.docmanagerweb.mapper.CategoryMapper;
import com.szr.docmanagerweb.mapper.DocumentMapper;
import com.szr.docmanagerweb.mapper.DocumentSectionMapper;
import com.szr.docmanagerweb.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final DocumentSectionMapper sectionMapper;
    private final DocumentMapper documentMapper;
    private final CategoryMapper categoryMapper;

    @Override
    public IPage<SearchResultDTO> searchSections(String keyword, int page, int size) {
        if (!StringUtils.hasText(keyword)) {
            // 关键词为空，返回空分页
            return new Page<>(page + 1L, size, 0);
        }

        // 1. 查询所有匹配片段（数据量小，直接查全量）
        LambdaQueryWrapper<DocumentSection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(DocumentSection::getTitle, keyword)
                .or()
                .like(DocumentSection::getContent, keyword);
        List<DocumentSection> sections = sectionMapper.selectList(queryWrapper);

        // 2. 计算相关度并排序
        List<DocumentSection> sortedSections = sections.stream()
                .sorted(Comparator.comparingInt((DocumentSection s) -> {
                            // 标题匹配优先
                            if (s.getTitle() != null && s.getTitle().contains(keyword)) {
                                return 2;
                            }
                            return 1;
                        }).reversed()
                        .thenComparing(Comparator.comparing(DocumentSection::getUpdateTime).reversed()))
                .toList();

        // 3. 手动分页
        int total = sortedSections.size();
        Page<SearchResultDTO> resultPage = new Page<>(page + 1L, size, total);
        int start = (int) Math.min((long) page * size, total);
        int end = Math.min(start + size, total);
        if (start > end) {
            start = end;
        }
        List<DocumentSection> pageSections = sortedSections.subList(start, end);

        // 4. 批量查询关联文档和分类
        List<Long> documentIds = pageSections.stream()
                .map(DocumentSection::getDocumentId)
                .distinct()
                .collect(Collectors.toList());
        List<Long> categoryIds = pageSections.stream()
                .map(DocumentSection::getCategoryId)
                .filter(java.util.Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, String> documentTitleMap = documentMapper.selectByIds(documentIds).stream()
                .collect(Collectors.toMap(Document::getId, Document::getTitle));
        Map<Long, String> categoryNameMap = categoryMapper.selectByIds(categoryIds).stream()
                .collect(Collectors.toMap(Category::getId, Category::getName));

        // 5. 转换为 DTO 列表
        List<SearchResultDTO> dtoList = pageSections.stream().map(section -> {
            SearchResultDTO dto = new SearchResultDTO();
            dto.setSectionId(section.getId());
            dto.setSectionTitle(section.getTitle());
            dto.setContentSnippet(extractSnippet(section.getContent(), keyword));
            dto.setDocumentTitle(documentTitleMap.getOrDefault(section.getDocumentId(), "未知文档"));
            dto.setCategoryName(section.getCategoryId() != null
                    ? categoryNameMap.getOrDefault(section.getCategoryId(), "未分类")
                    : "未分类");
            // 相关度
            if (section.getTitle() != null && section.getTitle().contains(keyword)) {
                dto.setRelevance(2);
            } else {
                dto.setRelevance(1);
            }
            return dto;
        }).collect(Collectors.toList());

        resultPage.setRecords(dtoList);
        return resultPage;
    }

    /**
     * 提取关键词附近的摘要
     * 截取关键词首次出现位置前后各50个字符
     *
     * @param content 正文纯文本
     * @param keyword 关键词
     * @return 摘要字符串
     */
    private String extractSnippet(String content, String keyword) {
        if (content == null) {
            return "";
        }
        int index = content.indexOf(keyword);
        if (index == -1) {
            // 理论上不会发生，因为查询已过滤
            return content.length() > 100 ? content.substring(0, 100) : content;
        }
        int start = Math.max(0, index - 50);
        int end = Math.min(content.length(), index + keyword.length() + 50);
        String snippet = content.substring(start, end);
        // 添加省略号
        if (start > 0) {
            snippet = "..." + snippet;
        }
        if (end < content.length()) {
            snippet = snippet + "...";
        }
        return snippet;
    }
}