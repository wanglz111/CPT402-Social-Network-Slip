package com.xjtlu.slip.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【topic】的数据库操作Mapper
* @createDate 2022-04-24 20:29:35
* @Entity com.xjtlu.slip.pojo.Topic
*/
@Repository
@Transactional
public interface TopicMapper extends BaseMapper<Topic> {
    List<Topic> getAllTopicsAndUser();

    Page<Topic> getAllTopicsAndUserByPage(IPage<Topic> page, QueryWrapper<Topic> queryWrapper);
}




