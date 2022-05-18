package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.CommentService;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private RedisService redisService;

    @GetMapping("/comment/{topicId}")
    public List<Comment> getComment(@PathVariable() String topicId){
        return commentService.getListByTopicId(topicId);
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("topicId") Long topicId, @RequestParam("replyContent") String content, @RequestParam("userId") Long replyUserId, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        // 判断用户与评论的用户是否一致，防止post攻击
        assert loginUser.getId().equals(replyUserId);
        //首先写入comment数据库
        Comment comment = new Comment();
        comment.setTopicId(topicId);
        comment.setContent(content);
        comment.setUserId(replyUserId);
        comment.setCreateTime(System.currentTimeMillis()/1000);
        commentService.save(comment);
        //然后更新父贴的最新评论时间
        Topic topic = topicService.getById(topicId);
        topic.setLatestCommentUnixTime(comment.getCreateTime());
        topic.setComment(topic.getComment()+1);
        topicService.saveOrUpdate(topic);
        //更新redis中的数据, 删掉相关redis中的数据, 等下次读取更新
        //todo:考虑使用消息队列来异步更新Redis
        redisService.refreshTopicRecord();
        return "redirect:/topic/d/"+topicId;
    }
}
