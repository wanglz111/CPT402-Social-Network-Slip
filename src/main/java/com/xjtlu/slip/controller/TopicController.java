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
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
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
        Page<Topic> page = new Page<>(1, 50);
        List<Topic> topics = topicService.page(page, queryWrapper).getRecords();
        topics.forEach(topic -> {
            Long latestCommentUnixTime = topic.getLatestCommentUnixTime();
            //set time format like xx seconds ago/xx minutes ago/xx hours ago/xx days ago
            if (latestCommentUnixTime != null) {
                Long now = getCurrentTime();
                long time = (now - topic.getLatestCommentUnixTime()) * 1000;
                topic.setLatestCommentTime(TimeFormat.format(time));
            }
        });
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
        model.addAttribute("topicClickCount", topicClickCount==null ? 0:topicClickCount);
        return "topicDetails";
    }

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        if (cookieUtil.getCookie("_userSession") != null) {
            Object rawData = redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
            if (rawData != null) {
                User user = (User) rawData;
                model.addAttribute("loginUser", user);
            }
        }
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        Page<Topic> page = new Page<>(2, 30);
        List<Topic> topics = topicService.getAllTopicsAndUser(page, queryWrapper).getRecords();
        Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
        Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
        Map<Long, User> userInfo = userService.getUserMap();
        log.info(userInfo.toString());
        topics.forEach(topic -> {
            Long latestCommentUnixTime = topic.getLatestCommentUnixTime();
            //set time format like xx seconds ago/xx minutes ago/xx hours ago/xx days ago
            if (latestCommentUnixTime != null) {
                Long now = getCurrentTime();
                long time = (now - topic.getLatestCommentUnixTime()) * 1000;
                topic.setLatestCommentTime(TimeFormat.format(time));
            }

            Integer commentCount = topicCommentCount.get(topic.getId()).getCommentCount();
            topic.setCommentCount(commentCount);
            Comment comment = latestCommentInfoEveryTopic.get(topic.getId());
            topic.setLatestComment(comment);
            try {
                User user = userInfo.get(comment.getUserId());
                log.info(comment.getUserId().toString());
                topic.setLatestCommentUser(user.getName());
            } catch (Exception e) {
                //todo 无评论则设置为空
                log.error(e.getMessage());
                topic.setLatestCommentUser("无评论");
            }

            String topicTitle = topic.getTitle();
            //判断中文加英文是否大于60字符,如果大于则截取
            try {
                String newString = new String(topicTitle.getBytes("GB2312"), StandardCharsets.ISO_8859_1);
                if (newString.length() > 60) {
                    topic.setTitle(topicTitle.substring(0, 30).concat("..."));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        model.addAttribute("topics", topics);
        return "index";
    }
}
