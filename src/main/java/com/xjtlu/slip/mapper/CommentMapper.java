package com.xjtlu.slip.mapper;

import com.xjtlu.slip.pojo.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
* @author wangluzhi
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2022-04-27 15:58:58
* @Entity com.xjtlu.slip.pojo.Comment
*/
public interface CommentMapper extends BaseMapper<Comment> {

//    @Select("select comment.id as id, topic_id, user_id, content, create_time, name, phone, password, email, avatar from comment, user where comment.id = #{id} and comment.user_id = user.id")
    Comment selectCommentAndUserByCommentId(String id);

    List<Comment> selectCommentAndUserByTopicId(String topicId);
}




