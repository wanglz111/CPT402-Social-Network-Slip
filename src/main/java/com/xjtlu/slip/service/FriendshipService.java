package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Friendship;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【friendship】的数据库操作Service
* @createDate 2022-05-13 20:22:49
*/
public interface FriendshipService extends IService<Friendship> {
    List<Friendship> getFriendshipsByUserId(Long userId);

    List<Long> getFriendIdsByUserId(Long userId);
}
