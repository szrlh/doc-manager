package com.szr.docmanagerweb.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.szr.docmanagerweb.dto.SearchResultDTO;
import com.szr.docmanagerweb.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 搜索控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 关键词搜索片段（标题优先）
     *
     * @param keyword 搜索关键词
     * @param page    页码
     * @param size    每页大小
     * @return 搜索结果分页
     */
    @GetMapping
    public IPage<SearchResultDTO> search(@RequestParam String keyword,
                                         @RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "10") int size) {
        return searchService.searchSections(keyword, page, size);
    }
}
