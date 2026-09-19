package com.szr.docmanagerweb.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.szr.docmanagerweb.entity.SectionTag;
import com.szr.docmanagerweb.entity.Tag;
import com.szr.docmanagerweb.mapper.SectionTagMapper;
import com.szr.docmanagerweb.mapper.TagMapper;
import com.szr.docmanagerweb.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签服务实现类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
    
    private final SectionTagMapper sectionTagMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag createTag(String name) {
        // 校验名称非空
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }

        // 检查是否已存在同名标签
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, name.trim());
        Tag existingTag = this.getOne(queryWrapper);
        if (existingTag != null) {
            throw new IllegalStateException("标签已存在: " + name);
        }

        // 创建新标签
        Tag tag = new Tag();
        tag.setName(name.trim());
        this.save(tag);
        return tag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Tag updateTag(Long id, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("标签名称不能为空");
        }
        Tag existing = this.getById(id);
        if (existing == null) {
            throw new IllegalArgumentException("标签不存在");
        }
        // 检查是否与其他标签重名
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Tag::getName, newName.trim()).ne(Tag::getId, id);
        if (this.count(queryWrapper) > 0) {
            throw new IllegalStateException("标签名称已存在");
        }
        existing.setName(newName.trim());
        this.updateById(existing);
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTag(Long tagId) {
        // 校验标签存在
        Tag tag = this.getById(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("标签不存在: " + tagId);
        }

        // 删除标签与片段的关联关系
        LambdaQueryWrapper<SectionTag> sectionTagWrapper = new LambdaQueryWrapper<>();
        sectionTagWrapper.eq(SectionTag::getTagId, tagId);
        sectionTagMapper.delete(sectionTagWrapper);

        // 删除标签
        this.removeById(tagId);
    }
}
