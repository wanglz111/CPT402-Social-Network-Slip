package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
* @author wangluzhi
* @description 针对表【user】的数据库操作Service
* @createDate 2022-04-22 18:54:37
*/
@Repository
public interface UserService extends IService<User> {

    /**
     * @description 密码校验
     */
    User getByUsernameAndPassword(String username, String password);

    /**
     * @description 根据用户名查询用户信息
     */
    User getByUsername(String username);

    /**
     * @description 查询所有用户存入map
     */
    Map<Long, User> getUserMap();
}
