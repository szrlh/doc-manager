package com.szr.docmanagerweb.controller;


import com.szr.docmanagerweb.entity.Tag;
import com.szr.docmanagerweb.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签管理控制器
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    @GetMapping
    public List<Tag> list() {
        return tagService.list();
    }

    /**
     * 创建标签
     *
     * @param name 标签名称
     * @return 创建后的标签
     */
    @PostMapping
    public Tag create(@RequestParam String name) {
        return tagService.createTag(name);
    }

    /**
     * 更新标签
     *
     * @param id   标签ID
     * @param name 新的标签名称
     * @return 更新后的标签
     */
    @PutMapping("/{id}")
    public Tag update(@PathVariable Long id, @RequestParam String name) {
        return tagService.updateTag(id, name);
    }

    /**
     * 删除标签
     *
     * @param id 标签ID
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        tagService.deleteTag(id);
    }
}
