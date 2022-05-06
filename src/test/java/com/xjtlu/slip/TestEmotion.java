package com.xjtlu.slip;

import com.xjtlu.slip.service.EmotionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestEmotion {
    @Autowired
    private EmotionService emotionService;

    @Test
    public void test(){
        System.out.println(emotionService.list());
    }
}
