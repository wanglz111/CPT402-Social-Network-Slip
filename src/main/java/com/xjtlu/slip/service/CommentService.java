package com.xjtlu.slip.service;

import com.xjtlu.slip.pojo.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【comment】的数据库操作Service
* @createDate 2022-04-27 15:58:58
*/
public interface CommentService extends IService<Comment> {

    List<Comment> getListByTopicId(String topicId);
}
