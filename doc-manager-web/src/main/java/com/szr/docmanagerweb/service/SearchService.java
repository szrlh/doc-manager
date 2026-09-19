package com.szr.docmanagerweb.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szr.docmanagerweb.dto.SearchResultDTO;

/**
 * 搜索服务接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface SearchService {

    /**
     * 关键词搜索片段（标题优先）
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果分页
     */
    IPage<SearchResultDTO> searchSections(String keyword, int page, int size);
}
