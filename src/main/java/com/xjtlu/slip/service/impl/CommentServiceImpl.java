package com.xjtlu.slip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.service.CommentService;
import com.xjtlu.slip.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【comment】的数据库操作Service实现
* @createDate 2022-04-27 15:58:58
*/
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment>
    implements CommentService{

    @Override
    public List<Comment> getListByTopicId(String topicId) {
        return baseMapper.selectCommentAndUserByTopicId(topicId);
    }
}




