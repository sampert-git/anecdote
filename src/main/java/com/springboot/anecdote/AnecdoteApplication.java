package com.springboot.anecdote;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.springboot.anecdote.dao")
@EnableCaching
@EnableTransactionManagement
public class AnecdoteApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(AnecdoteApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AnecdoteApplication.class, args);
        LOGGER.info("启动Anecdote应用！");
    }
}
