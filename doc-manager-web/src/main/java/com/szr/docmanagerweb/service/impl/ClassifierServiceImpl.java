package com.szr.docmanagerweb.service.impl;


import com.alibaba.fastjson2.JSON;
import com.szr.docmanagerweb.entity.Category;
import com.szr.docmanagerweb.entity.CategoryTraining;
import com.szr.docmanagerweb.mapper.CategoryMapper;
import com.szr.docmanagerweb.mapper.CategoryTrainingMapper;
import com.szr.docmanagerweb.service.ClassifierService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智能分类服务实现类（规则匹配 + 朴素贝叶斯）
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Service
@RequiredArgsConstructor
public class ClassifierServiceImpl implements ClassifierService {

    private final CategoryMapper categoryMapper;
    private final CategoryTrainingMapper trainingMapper;

    // 规则匹配与机器学习权重
    private static final double RULE_WEIGHT = 0.4;
    private static final double ML_WEIGHT = 0.6;
    // 训练样本最小数量，低于此值仅使用规则匹配
    private static final int MIN_TRAINING_SAMPLES = 20;

    @Override
    public Map<Long, Double> classify(String text) {
        Map<Long, Double> ruleScores = classifyByRules(text);
        Map<Long, Double> mlScores = classifyByBayes(text);

        // 融合得分
        Map<Long, Double> finalScores = new HashMap<>();
        Set<Long> allCategoryIds = new HashSet<>();
        allCategoryIds.addAll(ruleScores.keySet());
        allCategoryIds.addAll(mlScores.keySet());

        for (Long categoryId : allCategoryIds) {
            double ruleScore = ruleScores.getOrDefault(categoryId, 0.0);
            double mlScore = mlScores.getOrDefault(categoryId, 0.0);
            double finalScore = RULE_WEIGHT * ruleScore + ML_WEIGHT * mlScore;
            if (finalScore > 0) {
                finalScores.put(categoryId, finalScore);
            }
        }

        // 按值降序排序
        return finalScores.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /**
     * 规则匹配分类
     * 基于分类的 ruleKeywords 字段进行关键词命中统计
     *
     * @param text 文本内容
     * @return 分类ID与得分
     */
    private Map<Long, Double> classifyByRules(String text) {
        Map<Long, Double> scores = new HashMap<>();
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            String ruleKeywords = category.getRuleKeywords();
            if (ruleKeywords == null || ruleKeywords.trim().isEmpty()) {
                continue;
            }
            List<String> keywords = JSON.parseArray(ruleKeywords, String.class);
            int hitCount = 0;
            for (String kw : keywords) {
                if (text.contains(kw)) {
                    hitCount += countOccurrences(text, kw);
                }
            }
            if (hitCount > 0) {
                scores.put(category.getId(), (double) hitCount);
            }
        }
        // 归一化：除以最大命中数，得到0~1之间的值
        if (!scores.isEmpty()) {
            double max = Collections.max(scores.values());
            scores.replaceAll((k, v) -> v / max);
        }
        return scores;
    }

    /**
     * 朴素贝叶斯分类（简化版：基于词频的多项式模型）
     * 从训练数据中学习类别先验和条件概率
     *
     * @param text 文本内容
     * @return 分类ID与概率
     */
    private Map<Long, Double> classifyByBayes(String text) {
        // 查询训练数据
        List<CategoryTraining> trainingData = trainingMapper.selectList(null);
        if (trainingData.size() < MIN_TRAINING_SAMPLES) {
            // 训练样本不足，返回空映射
            return Collections.emptyMap();
        }

        // 构建类别文档计数和词频统计
        Map<Long, Integer> categoryDocCount = new HashMap<>();
        Map<Long, Map<String, Integer>> categoryWordCount = new HashMap<>();
        Map<Long, Integer> categoryTotalWords = new HashMap<>();
        int totalDocs = trainingData.size();
        Set<String> vocabulary = new HashSet<>();

        Pattern splitPattern = Pattern.compile("[\\s\\p{Punct}]+");
        for (CategoryTraining sample : trainingData) {
            Long catId = sample.getCategoryId();
            categoryDocCount.merge(catId, 1, Integer::sum);
            String content = sample.getContentText();
            // 简单分词：按空格和常见分隔符拆分，这里简化处理，可替换为更好的分词器
            String[] words = splitPattern.split(content);
            Map<String, Integer> wordFreq = categoryWordCount.computeIfAbsent(catId, k -> new HashMap<>());
            for (String word : words) {
                if (word.isEmpty()) {
                    continue;
                }
                wordFreq.merge(word, 1, Integer::sum);
                categoryTotalWords.merge(catId, 1, Integer::sum);
                vocabulary.add(word);
            }
        }

        // 计算先验概率和条件概率（使用拉普拉斯平滑）
        Map<Long, Double> priorProb = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : categoryDocCount.entrySet()) {
            priorProb.put(entry.getKey(), Math.log((double) entry.getValue() / totalDocs));
        }

        // 对输入文本分词
        String[] inputWords = text.split("[\\s\\p{Punct}]+");
        Map<Long, Double> scores = new HashMap<>();
        for (Long catId : categoryDocCount.keySet()) {
            double score = priorProb.get(catId);
            Map<String, Integer> wordFreq = categoryWordCount.get(catId);
            int totalWordsInCat = categoryTotalWords.getOrDefault(catId, 0);
            int vocabSize = vocabulary.size();
            for (String word : inputWords) {
                if (word.isEmpty()) {
                    continue;
                }
                int count = wordFreq.getOrDefault(word, 0);
                double prob = (count + 1.0) / (totalWordsInCat + vocabSize);
                score += Math.log(prob);
            }
            scores.put(catId, Math.exp(score)); // 转回概率空间（非归一化，后续可归一化）
        }

        // 归一化概率
        if (!scores.isEmpty()) {
            double sum = scores.values().stream().mapToDouble(Double::doubleValue).sum();
            if (sum > 0) {
                scores.replaceAll((k, v) -> v / sum);
            }
        }
        return scores;
    }

    /**
     * 统计子字符串出现次数
     */
    private int countOccurrences(String text, String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return 0;
        }
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }
}
