package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.szr.docmanagerweb.dto.DocumentDTO;
import com.szr.docmanagerweb.entity.Document;
import com.szr.docmanagerweb.entity.DocumentSection;
import com.szr.docmanagerweb.entity.SectionTag;
import com.szr.docmanagerweb.mapper.DocumentMapper;
import com.szr.docmanagerweb.mapper.DocumentSectionMapper;
import com.szr.docmanagerweb.mapper.SectionTagMapper;
import com.szr.docmanagerweb.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档服务实现类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document> implements DocumentService {
    
    private final DocumentMapper documentMapper;
    private final DocumentSectionMapper documentSectionMapper;
    private final SectionTagMapper sectionTagMapper;

    @Override
    public IPage<DocumentDTO> listDocuments(int page, int size, String keyword) {
        // 创建分页对象（页码+1？MyBatis-Plus 的 Page 默认页码从1开始，而 Controller 可能传0，需要统一）
        // 此处假设 Controller 已经将页码转换为从1开始，或者我们在 Service 内部处理
        Page<DocumentDTO> pageParam = new Page<>(page + 1, size); // 如果调用方从0开始，则加1
        return documentMapper.selectDocumentPageWithSectionCount(pageParam, keyword);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDocumentWithSections(Long documentId) {
        // 校验文档是否存在
        Document document = this.getById(documentId);
        if (document == null) {
            throw new IllegalArgumentException("文档不存在: " + documentId);
        }

        // 查询该文档下所有片段ID
        LambdaQueryWrapper<DocumentSection> sectionQueryWrapper = new LambdaQueryWrapper<>();
        sectionQueryWrapper.eq(DocumentSection::getDocumentId, documentId);
        List<DocumentSection> sections = documentSectionMapper.selectList(sectionQueryWrapper);
        List<Long> sectionIds = sections.stream().map(DocumentSection::getId).collect(Collectors.toList());

        // 删除片段-标签关联
        if (!sectionIds.isEmpty()) {
            LambdaQueryWrapper<SectionTag> sectionTagWrapper = new LambdaQueryWrapper<>();
            sectionTagWrapper.in(SectionTag::getSectionId, sectionIds);
            sectionTagMapper.delete(sectionTagWrapper);
        }

        // 删除片段
        documentSectionMapper.delete(sectionQueryWrapper);

        // 删除文档
        this.removeById(documentId);
    }
}
