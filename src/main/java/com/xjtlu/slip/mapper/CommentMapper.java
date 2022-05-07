package com.xjtlu.slip.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.xjtlu.slip.pojo.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* @author wangluzhi
* @description 针对表【comment】的数据库操作Mapper
* @createDate 2022-04-27 15:58:58
* @Entity com.xjtlu.slip.pojo.Comment
*/

@Repository
@Transactional
public interface CommentMapper extends BaseMapper<Comment> {

//    @Select("select comment.id as id, topic_id, user_id, content, create_time, name, phone, password, email, avatar from comment, user where comment.id = #{id} and comment.user_id = user.id")
    Comment selectOneCommentAndUser(@Param(Constants.WRAPPER) QueryWrapper<Comment> queryWrapper);

    List<Comment> selectCommentAndUserByTopicId(String topicId);

    @MapKey("topicId")
    Map<Long, Comment> selectLatestCommentEveryTopic();

}




