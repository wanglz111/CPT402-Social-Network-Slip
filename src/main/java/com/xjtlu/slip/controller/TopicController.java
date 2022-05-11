package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.*;
import com.xjtlu.slip.utils.CookieUtil;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xjtlu.slip.utils.TimeToUnix.getCurrentTime;

@Controller
@Slf4j
public class TopicController {

    @Autowired
    private TopicService topicService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private EmotionService emotionService;

    @GetMapping("/topic/d/{topicId}")
    public String topicDetails(Model model, @PathVariable String topicId) {
        //获取帖子详情
        Topic topic = topicService.getById(topicId);
        model.addAttribute("topic", topic);
        //获取帖子作者
        Long authorId = topic.getAuthorId();
        User user = userService.getById(authorId);
        model.addAttribute("user", user);
        //获取帖子评论
        List<Comment> comments = commentService.getListByTopicId(topicId);
        model.addAttribute("comments", comments);
        //获取帖子点击数
        Integer topicClickCount = (Integer) redisService.get("/topic/".concat(topicId));
        model.addAttribute("topicClickCount", topicClickCount==null ? 0:topicClickCount);
        return "topicDetails";
    }

    @GetMapping("/topic/{page}")
    public String topic(Model model, @PathVariable Integer page, HttpSession session) {
        Page<Topic> topicPage = topicService.getAllTopicsAndUser(page,20);
        List<Topic> topics = topicPage.getRecords();
        //获取每条topic评论数
        Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
        //获取最新评论
        Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
        //获取最新评论用户
        Map<Long, User> userInfo = userService.getUserMap();
        topics.forEach(topic -> {
            Long latestCommentUnixTime = topic.getLatestCommentUnixTime();
            //set time format like xx seconds ago/xx minutes ago/xx hours ago/xx days ago
            if (latestCommentUnixTime != null) {
                topic.setLatestCommentTime(TimeFormat.format(latestCommentUnixTime));
            }

            Integer commentCount = topicCommentCount.get(topic.getId()).getCommentCount();
            topic.setCommentCount(commentCount);
            Comment comment = latestCommentInfoEveryTopic.get(topic.getId());
            topic.setLatestComment(comment);
            if (comment != null){
                User user = userInfo.get(comment.getUserId());
                topic.setLatestReplyUser(user);
            }
        });
        //如果session中有用户信息,则获取用户的emotion信息
        if (session.getAttribute("loginUser") != null) {
            User user = (User) session.getAttribute("loginUser");
            List<Emotion> emotions = emotionService.getByUserId(user.getId());
            emotions.forEach(emotion -> {
                emotion.setTime(TimeFormat.format(emotion.getCreateTime()));
                    });
            //获取emotion相关信息
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            Integer topicCount = topicService.getTopicCount(user.getId());
            model.addAttribute("topicCount", topicCount);
            //todo 获取用户的聊天好友数
        }
        model.addAttribute("topics", topics);
        return "index";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/topic/1";
    }
}
