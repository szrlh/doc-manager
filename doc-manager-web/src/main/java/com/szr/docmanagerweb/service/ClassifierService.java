package com.szr.docmanagerweb.service;


import java.util.Map;

/**
 * 智能分类服务接口
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
public interface ClassifierService {

    /**
     * 根据文本内容预测分类，返回分类ID和置信度
     * @param text 文本内容
     * @return 分类ID到置信度的映射（按置信度降序）
     */
    Map<Long, Double> classify(String text);
}
