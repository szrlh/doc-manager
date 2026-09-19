package com.szr.docmanagerweb.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szr.docmanagerweb.entity.Category;

/**
 * 分类 Mapper 接口
 * 继承 MyBatis-Plus 的 BaseMapper，提供单表 CRUD 操作
 * 复杂查询可通过自定义方法实现
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface CategoryMapper extends BaseMapper<Category> {
    // 暂不需要自定义方法
}
