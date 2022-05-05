package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.mapper.UserMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* @author wangluzhi
* @description 针对表【user】的数据库操作Service实现
* @createDate 2022-04-22 18:54:37
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

    @Override
    public User getByUsernameAndPassword(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        queryWrapper.eq("password", password);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public User getByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name", username);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public Map<Long, User> getUserMap() {
        List<User> users = baseMapper.selectList(null);
        Map<Long, User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getId(), user);
        }
        return userMap;

    }


}




