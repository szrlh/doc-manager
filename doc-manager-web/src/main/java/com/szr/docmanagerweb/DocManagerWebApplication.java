package com.szr.docmanagerweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.szr.docmanagerweb.mapper")
public class DocManagerWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(DocManagerWebApplication.class, args);
    }
}
