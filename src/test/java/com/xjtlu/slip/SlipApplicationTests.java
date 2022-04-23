package com.xjtlu.slip;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;


@Slf4j
@SpringBootTest
class SlipApplicationTests {
    @Autowired
    DataSource dataSource;

    @Test
    void contextLoads() {
        log.info(String.valueOf(dataSource.getClass()));
    }

}
