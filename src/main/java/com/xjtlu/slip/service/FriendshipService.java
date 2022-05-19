package com.xjtlu.slip.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Friendship;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjtlu.slip.pojo.User;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【friendship】的数据库操作Service
* @createDate 2022-05-13 20:22:49
*/
public interface FriendshipService extends IService<Friendship> {
    List<Friendship> getFriendshipsByUserId(Long userId);

    List<Long> getFriendIdsByUserId(Long userId);

    List<User> getFriendsByUserId(Long userId);

    IPage<User> getFriendsByUserIdPage(IPage<User> page, Long userId);
}
