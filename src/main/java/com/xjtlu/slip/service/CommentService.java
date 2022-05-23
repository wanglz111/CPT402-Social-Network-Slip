package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface CommentService extends IService<Comment> {

    List<Comment> getListByTopicId(String topicId);

    Map<Long, Comment> getLatestCommentInfoEveryTopic();
}
