package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.CommentService;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.service.UserService;
import com.xjtlu.slip.utils.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private CookieUtil cookieUtil;

    //跳转到帖子主页
    @GetMapping("/topic")
    public String topic(Model model, HttpSession session) {
        if (cookieUtil.getCookie("_userSession") != null) {
            Object rawData = redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
            if (rawData != null) {
                User user = (User) rawData;
                model.addAttribute("loginUser", user);
            }
        } else {
            session.setAttribute("msg", "Your login has expired, please log in again");
            return "redirect:/login";
        }
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        Page<Topic> page = new Page<>(1, 20);
        List<Topic> topics = topicService.page(page, queryWrapper).getRecords();
//        List<Topic> topics = topicService.list(queryWrapper);
        model.addAttribute("topics", topics);
        return "topic";
    }

    @GetMapping("/topic/{topicId}")
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
        model.addAttribute("topicClickCount", topicClickCount==null?0:topicClickCount);
        return "topicDetails";
    }
}
