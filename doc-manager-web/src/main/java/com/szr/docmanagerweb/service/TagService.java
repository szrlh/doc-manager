package com.szr.docmanagerweb.service;


import com.baomidou.mybatisplus.spring.service.IService;
import com.szr.docmanagerweb.entity.Tag;

/**
 * 标签服务接口
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface TagService extends IService<Tag> {

    /**
     * 创建标签
     *
     * @param name 标签名称
     * @return 创建后的标签
     */
    Tag createTag(String name);

    /**
     * 更新标签
     *
     * @param id      标签ID
     * @param newName 新的标签名称
     * @return 创建后的标签
     */
    Tag updateTag(Long id, String newName);

    /**
     * 删除标签（同时清理关联关系）
     *
     * @param tagId 标签ID
     */
    void deleteTag(Long tagId);
}
