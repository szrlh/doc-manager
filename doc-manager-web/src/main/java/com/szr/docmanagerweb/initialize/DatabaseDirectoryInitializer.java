package com.szr.docmanagerweb.initialize;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 数据库目录初始化
 *
 * <p>在 Spring 容器刷新（数据源、Flyway 初始化）之前确保 SQLite 数据库目录存在，
 * 否则首次启动时 Flyway 无法打开数据库文件。</p>
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Slf4j
public final class DatabaseDirectoryInitializer {

    /**
     * 数据库目录配置项
     */
    private static final String DB_DIR_PROPERTY = "doc-manager.db-dir";

    private DatabaseDirectoryInitializer() {
    }

    /**
     * 在数据源初始化之前创建数据库目录
     *
     * @param environment 已加载配置项的环境对象
     */
    public static void ensureDatabaseDirectory(Environment environment) {
        String dbDir = environment.getProperty(DB_DIR_PROPERTY);
        if (StringUtils.isBlank(dbDir)) {
            throw new IllegalStateException("未配置数据库目录: " + DB_DIR_PROPERTY);
        }
        Path dir = Paths.get(dbDir);
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("无法创建数据库目录: " + dir.toAbsolutePath(), e);
        }
        log.info("数据库目录已就绪: {}", dir.toAbsolutePath());
    }
}
