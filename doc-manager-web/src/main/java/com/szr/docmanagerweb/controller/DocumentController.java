package com.szr.docmanagerweb.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szr.docmanagerweb.dto.DocumentDTO;
import com.szr.docmanagerweb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 文档管理控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    /**
     * 分页查询文档列表
     *
     * @param page    页码（从0开始）
     * @param size    每页大小
     * @param keyword 可选，标题关键词
     * @return 文档分页数据
     */
    @GetMapping
    public IPage<DocumentDTO> list(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   @RequestParam(required = false) String keyword) {
        return documentService.listDocuments(page, size, keyword);
    }

    /**
     * 删除文档及其所有片段
     *
     * @param id 文档ID
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        documentService.deleteDocumentWithSections(id);
    }
}
