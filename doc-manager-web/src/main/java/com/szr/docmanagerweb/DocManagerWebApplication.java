package com.szr.docmanagerweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.szr.docmanagerweb.mapper")
public class DocManagerWebApplication {

    /**
     * 应用启动入口，数据库目录由 DatabaseInitializerConfig 在容器刷新时创建
     *
     * @param args 启动参数
     */
    public static void main(String[] args) {
        SpringApplication.run(DocManagerWebApplication.class, args);
    }
}
