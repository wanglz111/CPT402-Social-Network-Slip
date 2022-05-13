package com.xjtlu.slip;

import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.SerializeUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@SpringBootTest
public class TestRedis {
    @Autowired
    UserService userService;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    RedisTemplate<String, Object> redisTemplate2 = new RedisTemplate<>();
    @Autowired
    private RedisService redisService;
    @Test
    public void test() {
        User user = userService.getById(1);
//        redisTemplate.opsForValue().set("user", "1");
        System.out.println(redisTemplate.opsForValue().get("/index"));
    }

    @Test
    public void test2() {
        List<User> users = userService.list();
        for (User user : users) {
            String key = "redis:Time:" + user.getId();
            redisService.set(key, user, 60 * 60 * 24);
            System.out.println(redisService.get(key));
        }
    }

    @Test
    public void test3() {
        String key = "User:userId:";
        List<User> users = userService.list();
        users.forEach(user -> {
            redisService.set(key + user.getId(), user);
                });
    }

    @Test
    public void test4() {
        Map<Long, User> userInfo = userService.getUserMap();
        redisService.set("User:AllUserInfo", userInfo);
    }


}
