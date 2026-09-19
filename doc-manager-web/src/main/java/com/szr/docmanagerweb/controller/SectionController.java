package com.szr.docmanagerweb.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szr.docmanagerweb.dto.SectionDTO;
import com.szr.docmanagerweb.service.SectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档片段管理控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */

@RestController
@RequestMapping("/api/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    /**
     * 分页查询片段，支持文档、分类、标签、关键词过滤
     *
     * @param page       页码
     * @param size       每页大小
     * @param documentId 文档ID（可选）
     * @param categoryId 分类ID（可选）
     * @param tagId      标签ID（可选）
     * @param keyword    关键词（可选）
     * @return 片段分页数据
     */
    @GetMapping
    public IPage<SectionDTO> list(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(required = false) Long documentId,
                                  @RequestParam(required = false) Long categoryId,
                                  @RequestParam(required = false) Long tagId,
                                  @RequestParam(required = false) String keyword) {
        return sectionService.listSections(page, size, documentId, categoryId, tagId, keyword);
    }

    @GetMapping("/{id}")
    public SectionDTO getById(@PathVariable Long id) {
        return sectionService.getSectionDetail(id);
    }

    /**
     * 更新片段内容（原始 Markdown），重新生成纯文本
     *
     * @param id              片段ID
     * @param originalContent 新的原始内容
     * @param title           新的标题（可选）
     */
    @PutMapping("/{id}")
    public void update(@PathVariable Long id,
                       @RequestParam String originalContent,
                       @RequestParam(required = false) String title,
                       @RequestParam(required = false) Long categoryId,
                       @RequestParam(required = false) List<Long> tagIds) {
        sectionService.updateSectionWithOriginalContent(id, originalContent, title, categoryId, tagIds);
    }

    /**
     * 删除片段
     *
     * @param id 片段ID
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        sectionService.deleteSection(id);
    }

    /**
     * 为片段添加标签
     *
     * @param id     片段ID
     * @param tagIds 标签ID列表
     */
    @PostMapping("/{id}/tags")
    public void addTags(@PathVariable Long id, @RequestBody List<Long> tagIds) {
        sectionService.addTags(id, tagIds);
    }

    /**
     * 移除片段标签
     *
     * @param id    片段ID
     * @param tagId 标签ID
     */
    @DeleteMapping("/{id}/tags/{tagId}")
    public void removeTag(@PathVariable Long id, @PathVariable Long tagId) {
        sectionService.removeTag(id, tagId);
    }
}
