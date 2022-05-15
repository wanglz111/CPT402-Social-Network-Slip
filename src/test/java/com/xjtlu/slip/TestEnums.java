package com.xjtlu.slip;

import com.xjtlu.slip.enums.TopicEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;


public class TestEnums {
    @Test
    public void testEnums() {
        System.out.println(Arrays.toString(TopicEnum.values()));
    }
}
