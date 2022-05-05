package com.xjtlu.slip.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
* @author wangluzhi
* @description 针对表【topic】的数据库操作Service
* @createDate 2022-04-24 20:29:35
*/
@Repository
public interface TopicService extends IService<Topic> {

    Page<Topic> getAllTopicsAndUser(Page<Topic> page, QueryWrapper<Topic> queryWrapper);

    Map<Long, Integer> getCommentCount();
}
