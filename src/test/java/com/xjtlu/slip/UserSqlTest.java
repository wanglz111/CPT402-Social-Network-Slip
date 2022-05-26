package com.xjtlu.slip;

import cn.hutool.crypto.digest.MD5;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class UserSqlTest {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @Test
    public void test() {
        List<User> users = userService.list();
//        for (User user : users) {
//            System.out.println(user);
//        }
        Map<Long, String> map = new HashMap<>();
        for (User user : users) {
            redisService.set(String.valueOf(user.getId()), MD5.create().digestHex(user.toString()));
        }

    }
}
