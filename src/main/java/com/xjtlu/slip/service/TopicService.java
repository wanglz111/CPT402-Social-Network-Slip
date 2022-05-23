package com.xjtlu.slip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjtlu.slip.vo.CommentCount;
import org.springframework.stereotype.Repository;

import java.util.Map;


@Repository
public interface TopicService extends IService<Topic> {

    /**
     * @description Check the latest by join table
     */
    Page<Topic> getAllTopicsAndUser(Integer page, Integer size);

    /**
     * @description get all comments
     */
    Map<Long, CommentCount> getCommentCount();

    int getTopicCount(Long id);

    Page<Topic> getAllTopicsAndUserByType(Integer page, Integer size, Long typeId);

    Page<Topic> getAllTopicsByUser(Integer page, Integer size, Long userId);
}
