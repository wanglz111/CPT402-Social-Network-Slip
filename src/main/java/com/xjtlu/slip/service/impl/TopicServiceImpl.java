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

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic>
    implements TopicService{

    @Autowired
    private TopicMapper topicMapper;

    @Override
    public Page<Topic> getAllTopicsAndUser(Integer currentPage, Integer pageSize) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        queryWrapper.eq("is_deleted",0);
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

    @Override
    public Page<Topic> getAllTopicsAndUserByType(Integer current, Integer size, Long typeId) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        queryWrapper.eq("type", typeId);
        queryWrapper.eq("is_deleted",0);
        Page<Topic> page = new Page<>(current, size);
        return topicMapper.getAllTopicsAndUserByPage(page, queryWrapper);
    }

    @Override
    public Page<Topic> getAllTopicsByUser(Integer current, Integer size, Long userId) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        queryWrapper.eq("author_id", userId);
        queryWrapper.eq("is_deleted",0);
        Page<Topic> page = new Page<>(current, size);
        return topicMapper.getAllTopicsAndUserByPage(page, queryWrapper);
    }
}




