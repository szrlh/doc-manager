package com.szr.docmanagerweb.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * 数据库初始化配置类
 * 
 * @author hao liu
 * @version 1.0
 * @since 2026/09/04
 */
@Configuration
public class DatabaseInitializerConfig {

    @Value("${doc-manager.db-dir}")
    private String dbDir;

    /**
     * 在数据源初始化之前创建数据库目录
     */
    @Bean
    public Void createDatabaseDirectory() {
        File dir = new File(dbDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new IllegalStateException("无法创建数据库目录: " + dbDir);
            }
        }
        return null;
    }
}
