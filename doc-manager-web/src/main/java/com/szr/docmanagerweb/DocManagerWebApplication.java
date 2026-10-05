package com.szr.docmanagerweb;

import com.szr.docmanagerweb.initialize.DatabaseDirectoryInitializer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.szr.docmanagerweb.mapper")
public class DocManagerWebApplication {

    /**
     * 应用启动入口，在容器刷新前注册数据库目录初始化器
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DocManagerWebApplication.class);
        application.addInitializers(context -> DatabaseDirectoryInitializer.ensureDatabaseDirectory(context.getEnvironment()));
        application.run(args);
    }
}
