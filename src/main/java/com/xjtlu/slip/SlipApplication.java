package com.xjtlu.slip;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(scanBasePackages = {"com.xjtlu.slip"})
@EnableCaching
public class SlipApplication {

    public static void main(String[] args) {
        SpringApplication.run(SlipApplication.class, args);
    }

}
