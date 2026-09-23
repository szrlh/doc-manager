package com.szr.docmanagerweb.util;


import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 时间工具类
 * 提供统一的时间格式化器和常用时间操作
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@UtilityClass
public final class DateUtils {

    /**
     * 默认时区 北京时间
     */
    public static final ZoneId DEFAULT_ZONE = ZoneId.of("Asia/Shanghai");


    /**
     * 统一的时间格式：yyyy-MM-dd HH:mm:ss
     */
    public static final DateTimeFormatter STANDARD_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前时间字符串（标准格式）
     *
     * @return 格式化的当前时间
     */
    public static String now() {
        return LocalDateTime.now(DEFAULT_ZONE).format(STANDARD_FORMATTER);
    }

    /**
     * 格式化指定时间
     *
     * @param dateTime 待格式化的时间
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(STANDARD_FORMATTER);
    }
}
