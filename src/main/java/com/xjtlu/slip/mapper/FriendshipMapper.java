package com.xjtlu.slip.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xjtlu.slip.pojo.Friendship;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjtlu.slip.pojo.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【friendship】的数据库操作Mapper
* @createDate 2022-05-13 20:22:49
* @Entity com.xjtlu.slip.pojo.Friendship
*/
public interface FriendshipMapper extends BaseMapper<Friendship> {
    List<Long> getFriendIdsByUserId(Long userId);

    List<User> getFriendEntitiesByUserId(Long userId);

    IPage<User> getFriendEntitiesByUserId(IPage<User> page, Long userId);
}




