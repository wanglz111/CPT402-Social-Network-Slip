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

    User getByUsernameAndPassword(String username, String password);

    User getByUsername(String username);

    Map<Long, User> getUserMap();
}
