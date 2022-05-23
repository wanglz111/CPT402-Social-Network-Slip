package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public interface UserService extends IService<User> {

    /**
     * @description Password verification
     */
    User getByUsernameAndPassword(String username, String password);

    /**
     * @description Query user information by user name
     */
    User getByUsername(String username);

    /**
     * @description Query all users stored in map
     */
    Map<Long, User> getUserMap();
}
