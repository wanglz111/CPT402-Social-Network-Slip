package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.mapper.TopicMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【topic】的数据库操作Service实现
* @createDate 2022-04-24 20:29:35
*/
@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
    implements TopicService{

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public List<Topic> getAllTopicsAndUser() {
        return topicMapper.getAllTopicsAndUser();
    }
}




