package com.xjtlu.slip.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xjtlu.slip.vo.CommentCount;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
* @author wangluzhi
* @description 针对表【topic】的数据库操作Service
* @createDate 2022-04-24 20:29:35
*/
@Repository
public interface TopicService extends IService<Topic> {

    /**
     * @description 并表查最新
     */
    Page<Topic> getAllTopicsAndUser(Integer page, Integer size);

    /**
     * @description 获取所有的评论数
     */
    Map<Long, CommentCount> getCommentCount();

    int getTopicCount(Long id);
}
