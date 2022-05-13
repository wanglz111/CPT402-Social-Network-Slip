package com.xjtlu.slip.mapper;

import com.xjtlu.slip.pojo.Friendship;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【friendship】的数据库操作Mapper
* @createDate 2022-05-13 20:22:49
* @Entity com.xjtlu.slip.pojo.Friendship
*/
public interface FriendshipMapper extends BaseMapper<Friendship> {
    List<Long> getFriendIdsByUserId(Long userId);
}




