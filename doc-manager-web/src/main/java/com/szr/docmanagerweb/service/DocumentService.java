package com.szr.docmanagerweb.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.szr.docmanagerweb.dto.DocumentDTO;
import com.szr.docmanagerweb.entity.Document;

/**
 * 文档服务接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface DocumentService extends IService<Document> {

    /**
     * 分页查询文档列表（包含片段数量）
     * @param page 页码
     * @param size 每页大小
     * @param keyword 标题关键词
     * @return 文档分页数据
     */
    IPage<DocumentDTO> listDocuments(int page, int size, String keyword);

    /**
     * 删除文档及其所有片段
     * @param documentId 文档ID
     */
    void deleteDocumentWithSections(Long documentId);
}
