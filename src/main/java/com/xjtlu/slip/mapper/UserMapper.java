package com.xjtlu.slip.mapper;

import com.xjtlu.slip.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
* @author wangluzhi
* @description 针对表【user】的数据库操作Mapper
* @createDate 2022-04-22 18:54:37
* @Entity com.xjtlu.slip.pojo.User
*/
//@Mapper
@Repository
@Transactional
public interface UserMapper extends BaseMapper<User> {
}




