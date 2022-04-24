package com.xjtlu.slip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Repository;

@SpringBootApplication
//@MapperScan("com.xjtlu.slip.mapper")
//@ComponentScan(basePackages = {"com.xjtlu.slip.service", "com.xjtlu.slip.controller", "com.xjtlu.slip.pojo", "com.xjtlu.slip.configs"})
public class SlipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlipApplication.class, args);
    }

}