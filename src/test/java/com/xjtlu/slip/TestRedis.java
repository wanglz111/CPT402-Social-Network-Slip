package com.xjtlu.slip;

import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.SerializeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.Objects;

@SpringBootTest
public class TestRedis {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Test
    public void test() {
        User user = userService.getById(1);
        redisTemplate.opsForValue().set("user", "1");
        System.out.println(redisTemplate.opsForValue().get("user"));
    }
}
