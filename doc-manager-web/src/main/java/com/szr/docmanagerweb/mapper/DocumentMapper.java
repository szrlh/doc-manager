package com.szr.docmanagerweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.szr.docmanagerweb.dto.DocumentDTO;
import com.szr.docmanagerweb.entity.Document;
import org.apache.ibatis.annotations.Param;

/**
 * 文档 Mapper 接口
 * 提供文档表的基础操作及自定义分页查询
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface DocumentMapper extends BaseMapper<Document> {

    /**
     * 分页查询文档列表，并统计每个文档的片段数量
     * 使用 LEFT JOIN 关联片段表进行计数，支持按标题模糊过滤
     *
     * @param page    分页参数（MyBatis-Plus 的 Page 对象）
     * @param keyword 标题关键词（可空）
     * @return 分页的 DocumentDTO 列表（包含片段数量）
     */
    IPage<DocumentDTO> selectDocumentPageWithSectionCount(Page<DocumentDTO> page, @Param("keyword") String keyword);
}
