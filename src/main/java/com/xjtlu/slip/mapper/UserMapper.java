package com.xjtlu.slip.mapper;

import com.xjtlu.slip.pojo.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;



@Repository
@Transactional
public interface UserMapper extends BaseMapper<User> {
}




