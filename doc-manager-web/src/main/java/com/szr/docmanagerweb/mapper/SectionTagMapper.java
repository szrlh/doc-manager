package com.szr.docmanagerweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szr.docmanagerweb.entity.SectionTag;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 片段-标签关联 Mapper 接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface SectionTagMapper extends BaseMapper<SectionTag> {

    /**
     * 批量插入片段-标签关联
     * 使用一条 INSERT 语句插入多条记录，提高性能
     *
     * @param list 待插入的关联列表
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<SectionTag> list);
}
