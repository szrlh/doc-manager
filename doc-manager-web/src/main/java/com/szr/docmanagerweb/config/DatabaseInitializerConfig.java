package com.szr.docmanagerweb.config;


import com.szr.docmanagerweb.initialize.DatabaseDirectoryInitializer;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * 数据库初始化配置类
 *
 * <p>以 {@link BeanFactoryPostProcessor} 的形式在单例预实例化之前创建 SQLite 数据库目录，
 * 保证早于数据源、Flyway 使用数据库文件，避免首次启动报 SQLITE_CANTOPEN。</p>
 *
 * <p>本类不存在 @Bean 方法之间的相互调用，声明 {@code proxyBeanMethods = false} 关闭 CGLIB 增强，
 * 配置类不再要求存在可见构造器。</p>
 *
 * @author hao liu
 * @version 1.0
 * @since 2026/10/05
 */
@Configuration(proxyBeanMethods = false)
public class DatabaseInitializerConfig {

    private DatabaseInitializerConfig() {
    }

    /**
     * 数据库目录初始化后置处理器，执行时机早于普通单例 Bean 的实例化
     *
     * @param environment 已加载配置项的环境对象
     * @return 数据库目录初始化后置处理器
     */
    @Bean
    public static BeanFactoryPostProcessor databaseDirectoryPostProcessor(Environment environment) {
        return beanFactory -> DatabaseDirectoryInitializer.ensureDatabaseDirectory(environment);
    }
}
