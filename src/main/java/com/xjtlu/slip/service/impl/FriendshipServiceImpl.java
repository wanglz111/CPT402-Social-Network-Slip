package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Friendship;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.FriendshipService;
import com.xjtlu.slip.mapper.FriendshipMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【friendship】的数据库操作Service实现
* @createDate 2022-05-13 20:22:49
*/
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship>
    implements FriendshipService {

    @Override
    public List<Friendship> getFriendshipsByUserId(Long userId) {
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.or().eq("friend_id", userId);
        return this.list(queryWrapper);
    }

    @Override
    public List<Long> getFriendIdsByUserId(Long userId) {
        return baseMapper.getFriendIdsByUserId(userId);
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        return baseMapper.getFriendEntitiesByUserId(userId);
    }

    @Override
    public IPage<User> getFriendsByUserIdPage(IPage<User> page, Long userId) {
        return baseMapper.getFriendEntitiesByUserId(page, userId);
    }

}






