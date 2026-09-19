package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.szr.docmanagerweb.dto.*;
import com.szr.docmanagerweb.entity.*;
import com.szr.docmanagerweb.mapper.*;
import com.szr.docmanagerweb.service.ClassifierService;
import com.szr.docmanagerweb.service.ImportService;
import com.szr.docmanagerweb.util.DateUtils;
import com.szr.docmanagerweb.util.MarkdownUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 文档导入服务实现类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService {
    
    private final ClassifierService classifierService;
    private final DocumentMapper documentMapper;
    private final DocumentSectionMapper documentSectionMapper;
    private final DocumentSectionMapper sectionMapper;
    private final SectionTagMapper sectionTagMapper;
    private final CategoryTrainingMapper trainingMapper;
    private final CategoryMapper categoryMapper;

    private static class HeadingInfo {
        int level;
        String text;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<ImportResultDTO> importDocument(MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }

        String filename = file.getOriginalFilename();
        String fileType = determineFileType(filename);
        String content = readFileContent(file);

        // 拆分片段
        List<ImportedSectionDTO> importedSections = parseSections(content, fileType);
        Set<Long> allCategoryIds = new HashSet<>();

        // 为每个片段调用分类器获取建议
        for (ImportedSectionDTO section : importedSections) {
            Map<Long, Double> classifyResult = classifierService.classify(section.getContent());
            List<CategorySuggestionDTO> suggestions = new ArrayList<>();
            if (classifyResult != null) {
                classifyResult.entrySet().stream()
                        .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                        .forEach(entry -> {
                            CategorySuggestionDTO suggestion = new CategorySuggestionDTO();
                            suggestion.setCategoryId(entry.getKey());
                            suggestion.setConfidence(entry.getValue());
                            suggestions.add(suggestion);
                            // // 收集分类 ID
                            allCategoryIds.add(entry.getKey());
                        });
            }
            section.setCategorySuggestions(suggestions);
        }

        // 批量查询分类名称
        Map<Long, String> categoryNameMap = new HashMap<>();
        if (!allCategoryIds.isEmpty()) {
            List<Category> categories = categoryMapper.selectByIds(allCategoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(Category::getId, Category::getName));
        }

        // 填充分类名称
        for (ImportedSectionDTO section : importedSections) {
            for (CategorySuggestionDTO suggestion : section.getCategorySuggestions()) {
                suggestion.setCategoryName(categoryNameMap.getOrDefault(suggestion.getCategoryId(), ""));
            }
        }

        ImportResultDTO result = new ImportResultDTO();
        result.setDocumentTitle(extractDocumentTitle(filename, content, fileType));
        result.setFileType(fileType);
        result.setSections(importedSections);

        return Collections.singletonList(result);
    }
    
    @Override
    public Long confirmImport(ImportConfirmDTO confirmDTO) {
        if (confirmDTO == null || confirmDTO.getSections() == null || confirmDTO.getSections().isEmpty()) {
            throw new IllegalArgumentException("导入数据不能为空");
        }

        // 1. 根据文档标题查找已有文档
        LambdaQueryWrapper<Document> docQuery = new LambdaQueryWrapper<>();
        docQuery.eq(Document::getTitle, confirmDTO.getDocumentTitle());
        Document document = documentMapper.selectOne(docQuery);

        String now = DateUtils.now();
        
        if (document != null) {
            // 存在同标题文档，更新元数据
            document.setFileType(confirmDTO.getFileType());
            document.setUpdateTime(now);
            documentMapper.updateById(document);

            // 删除该文档下所有旧分片（级联删除标签关联）
            LambdaQueryWrapper<DocumentSection> sectionQuery = new LambdaQueryWrapper<>();
            sectionQuery.eq(DocumentSection::getDocumentId, document.getId());
            documentSectionMapper.delete(sectionQuery);
        } else {
            // 创建新文档
            document = new Document();
            document.setTitle(confirmDTO.getDocumentTitle());
            document.setFileType(confirmDTO.getFileType());
            document.setCreateTime(now);
            document.setUpdateTime(now);
            documentMapper.insert(document);
        }

        Long documentId = document.getId();

        // 2. 批量创建片段
        List<ImportedSectionConfirmDTO> sections = confirmDTO.getSections();
        int orderIndex = 1;
        for (ImportedSectionConfirmDTO sec : sections) {
            DocumentSection section = new DocumentSection();
            section.setDocumentId(documentId);
            section.setTitle(sec.getTitle());
            section.setOriginalContent(sec.getOriginalContent());
            section.setContent(sec.getContent());
            section.setOrderIndex(orderIndex++);
            section.setCategoryId(sec.getCategoryId());
            section.setCreateTime(now);
            section.setUpdateTime(now);
            sectionMapper.insert(section);

            // 3. 插入标签关联
            if (sec.getTagIds() != null && !sec.getTagIds().isEmpty()) {
                for (Long tagId : sec.getTagIds()) {
                    SectionTag st = new SectionTag();
                    st.setSectionId(section.getId());
                    st.setTagId(tagId);
                    sectionTagMapper.insert(st);
                }
            }

            // 4. 记录训练数据（用于分类器训练）
            if (sec.getCategoryId() != null) {
                CategoryTraining training = new CategoryTraining();
                // 截取前500字符
                training.setContentText(truncateText(sec.getContent(), 500));
                training.setCategoryId(sec.getCategoryId());
                training.setCreateTime(now);
                trainingMapper.insert(training);
            }
        }

        return documentId;
    }

    /**
     * 根据文件名确定文件类型
     */
    private String determineFileType(String filename) {
        if (filename == null) return "txt";
        String lower = filename.toLowerCase();
        if (lower.endsWith(".md") || lower.endsWith(".markdown")) {
            return "md";
        }
        return "txt";
    }

    /**
     * 读取文件内容为字符串（UTF-8）
     */
    private String readFileContent(MultipartFile file) {
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败", e);
        }
    }

    /**
     * 解析文档内容为片段列表
     * 根据文件类型采用不同拆分规则
     */
    private List<ImportedSectionDTO> parseSections(String content, String fileType) {
        List<ImportedSectionDTO> sections;
        if ("md".equals(fileType)) {
            sections = parseMarkdown(content);
        } else {
            sections = parsePlainText(content);
        }
        return sections;
    }

    /**
     * Markdown 拆分：按标题行拆分
     */
    private List<ImportedSectionDTO> parseMarkdown(String content) {
        List<ImportedSectionDTO> result = new ArrayList<>();
        String[] lines = content.split("\n", -1);

        Deque<HeadingInfo> titleStack = new ArrayDeque<>();
        StringBuilder currentContent = new StringBuilder();
        String currentTitle = null;
        boolean currentHasContent = false;
        boolean inCodeBlock = false;

        for (String line : lines) {
            // 代码块切换检测
            if (line.trim().startsWith("```")) {
                inCodeBlock = !inCodeBlock;
                currentContent.append(line).append("\n");
                if (!inCodeBlock) {
                    currentHasContent = true; // 退出代码块，内容有效
                }
                continue;
            }

            // 代码块内所有行视为内容
            if (inCodeBlock) {
                currentContent.append(line).append("\n");
                currentHasContent = true;
                continue;
            }

            // 标题行检测
            Matcher matcher = Pattern.compile("^(#{1,6})\\s+(.*)").matcher(line);
            if (matcher.find()) {
                int level = matcher.group(1).length();
                String titleText = matcher.group(2).trim();

                // 保存上一个分片（如果有内容）
                if (currentTitle != null && currentHasContent) {
                    ImportedSectionDTO section = new ImportedSectionDTO();
                    section.setTitle(currentTitle);
                    section.setOriginalContent(currentContent.toString());
                    section.setContent(MarkdownUtils.markdownToPlainText(currentContent.toString()));
                    List<String> suggestedTags = new ArrayList<>();
                    for (HeadingInfo info : titleStack) {
                        if (!info.text.equals(currentTitle)) {
                            suggestedTags.add(info.text);
                        }
                    }
                    section.setSuggestedTags(suggestedTags);
                    result.add(section);
                }

                // 更新标题栈
                while (!titleStack.isEmpty() && titleStack.peek().level >= level) {
                    titleStack.pop();
                }
                HeadingInfo heading = new HeadingInfo();
                heading.level = level;
                heading.text = titleText;
                titleStack.push(heading);

                // 重置当前分片（标题行不加入内容）
                currentTitle = titleText;
                currentContent = new StringBuilder();
                currentHasContent = false;
            } else {
                // 普通文本行
                if (currentTitle != null) {
                    currentContent.append(line).append("\n");
                    if (!line.trim().isEmpty()) {
                        currentHasContent = true;
                    }
                }
            }
        }

        // 处理最后一个分片
        if (currentTitle != null && currentHasContent) {
            ImportedSectionDTO section = new ImportedSectionDTO();
            section.setTitle(currentTitle);
            section.setOriginalContent(currentContent.toString());
            section.setContent(MarkdownUtils.markdownToPlainText(currentContent.toString()));
            List<String> suggestedTags = new ArrayList<>();
            for (HeadingInfo info : titleStack) {
                if (!info.text.equals(currentTitle)) {
                    suggestedTags.add(info.text);
                }
            }
            section.setSuggestedTags(suggestedTags);
            result.add(section);
        }

        // 无有效分片时整篇作为一个分片
        if (result.isEmpty()) {
            ImportedSectionDTO single = new ImportedSectionDTO();
            single.setTitle(null);
            single.setOriginalContent(content);
            single.setContent(MarkdownUtils.markdownToPlainText(content));
            single.setSuggestedTags(new ArrayList<>());
            single.setOrderIndex(1);
            result.add(single);
        } else {
            for (int i = 0; i < result.size(); i++) {
                result.get(i).setOrderIndex(i + 1);
            }
        }
        return result;
    }

    /**
     * 纯文本拆分：按连续空三行拆分
     */
    private List<ImportedSectionDTO> parsePlainText(String content) {
        List<ImportedSectionDTO> result = new ArrayList<>();
        String[] parts = content.split("\\n\\s*\\n\\s*\\n"); // 空三行拆分
        int order = 1;
        for (String part : parts) {
            String trimmedPart = part.trim();
            if (trimmedPart.isEmpty()) continue;
            ImportedSectionDTO dto = new ImportedSectionDTO();
            // 提取首行作为标题（截取50字符）
            String firstLine = trimmedPart.split("\\n")[0].trim();
            if (firstLine.length() > 50) {
                firstLine = firstLine.substring(0, 50);
            }
            dto.setTitle(firstLine);
            dto.setOriginalContent(trimmedPart);
            dto.setContent(trimmedPart); // TXT 内容纯文本，与原内容相同
            dto.setOrderIndex(order++);
            dto.setSuggestedTags(new ArrayList<>());
            result.add(dto);
        }
        if (result.isEmpty()) {
            ImportedSectionDTO single = new ImportedSectionDTO();
            single.setTitle(null);
            single.setOriginalContent(content);
            single.setContent(content);
            single.setOrderIndex(1);
            single.setSuggestedTags(new ArrayList<>());
            result.add(single);
        }
        return result;
    }

    /**
     * 提取文档标题：优先使用 Markdown 第一个一级标题，否则使用文件名（此处用文件名代替）
     */
    private String extractDocumentTitle(String filename, String content, String fileType) {
        if ("md".equals(fileType)) {
            String[] lines = content.split("\n");
            for (String line : lines) {
                if (line.matches("^#\\s+.*")) {
                    return line.replaceAll("^#\\s+", "").trim();
                }
            }
        }
        // 回退到文件名（去掉扩展名）
        if (filename != null && filename.contains(".")) {
            return filename.substring(0, filename.lastIndexOf('.'));
        }
        return filename != null ? filename : "未命名文档";
    }

    /**
     * 截断文本到指定长度
     */
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength);
    }
}
