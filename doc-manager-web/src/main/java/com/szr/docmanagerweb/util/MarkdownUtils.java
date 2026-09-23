package com.szr.docmanagerweb.util;


import lombok.experimental.UtilityClass;

/**
 * Markdown 转纯文本工具类
 * 提供简单的 Markdown 标记去除功能
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@UtilityClass
public class MarkdownUtils {
    
    /**
     * 将 Markdown 格式文本转换为纯文本
     * 去除常见标记：标题、强调、代码块、列表、链接、图片、引用等
     *
     * @param markdown 原始 Markdown 文本
     * @return 纯文本
     */
    public static String markdownToPlainText(String markdown) {
        if (markdown == null || markdown.isEmpty()) {
            return "";
        }
        String text = markdown;
        // 去除代码围栏
        text = text.replaceAll("```[\\s\\S]*?```", "");
        // 去除行内代码
        text = text.replaceAll("`([^`]*)`", "$1");
        // 去除标题符号
        text = text.replaceAll("(?m)^#{1,6}\\s*", "");
        // 去除强调符号（*、_）
        text = text.replaceAll("[*_]{1,3}", "");
        // 去除无序列表标记
        text = text.replaceAll("(?m)^\\s*[-+*]\\s+", "");
        // 去除有序列表标记
        text = text.replaceAll("(?m)^\\s*\\d+\\.\\s+", "");
        // 去除链接 [text](url)
        text = text.replaceAll("\\[([^]]*)]\\([^)]*\\)", "$1");
        // 去除图片 ![alt](url)
        text = text.replaceAll("!\\[([^]]*)]\\([^)]*\\)", "$1");
        // 去除引用符号
        text = text.replaceAll("(?m)^>\\s*", "");
        // 压缩多余空行
        text = text.replaceAll("\\n{3,}", "\n\n").trim();
        return text;
    }
}
