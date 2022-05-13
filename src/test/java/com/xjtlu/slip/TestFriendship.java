package com.xjtlu.slip;

import com.xjtlu.slip.service.FriendshipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestFriendship {
    @Autowired
    private FriendshipService friendshipService;

    @Test
    public void test() {
        System.out.println(friendshipService.getFriendshipsByUserId(1L));
    }

    @Test
    public void testIdsList() {
        System.out.println(friendshipService.getFriendIdsByUserId(1L));
    }
}
