package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.mapper.TopicMapper;
import com.xjtlu.slip.vo.CommentCount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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
    public Page<Topic> getAllTopicsAndUser(Integer currentPage, Integer pageSize) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        Page<Topic> page = new Page<>(currentPage, pageSize);
        return topicMapper.getAllTopicsAndUserByPage(page, queryWrapper);
    }

    @Override
    public Map<Long, CommentCount> getCommentCount() {
       return topicMapper.getTopicCommentsCount();
    }

    @Override
    public int getTopicCount(Long id) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("author_id", id);
        List<Topic> topics = topicMapper.selectList(queryWrapper);
        return topics.size();
    }
}




