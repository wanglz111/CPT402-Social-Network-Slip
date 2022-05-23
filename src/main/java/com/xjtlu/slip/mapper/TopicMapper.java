package com.xjtlu.slip.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Topic;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xjtlu.slip.vo.CommentCount;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


@Repository
@Transactional
public interface TopicMapper extends BaseMapper<Topic> {
    List<Topic> getAllTopicsAndUser();

    Page<Topic> getAllTopicsAndUserByPage(IPage<Topic> page, @Param(Constants.WRAPPER) QueryWrapper<Topic> queryWrapper);

    Page<Topic> getAllTopicsAndAllComments(IPage<Topic> page, @Param(Constants.WRAPPER) QueryWrapper<Topic> queryWrapper);

    @MapKey("topicId")
    Map<Long, CommentCount> getTopicCommentsCount();
}




