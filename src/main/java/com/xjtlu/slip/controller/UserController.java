package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.*;
import com.xjtlu.slip.utils.TimeFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/user/{id}")
    public String userPage(@PathVariable String id, HttpSession session, Model model) {
        // get user info
        User user = userService.getById(id);
        model.addAttribute("user", user);
        // get user's topics
        QueryWrapper<Topic> topicQueryWrapper = new QueryWrapper<>();
        topicQueryWrapper.eq("author_id", id);
        topicQueryWrapper.orderByDesc("create_unix_time");
        Page<Topic> topicPage = topicService.page(new Page<>(1, 10), topicQueryWrapper);
        topicPage.getRecords().forEach(topic -> {
            topic.setLatestCommentTime(TimeFormat.format(topic.getLatestCommentUnixTime()));
                });
        model.addAttribute("topicPage", topicPage);
        // get user's emotions
        QueryWrapper<Emotion> emotionQueryWrapper = new QueryWrapper<>();
        emotionQueryWrapper.eq("user_id", id);
        emotionQueryWrapper.orderByDesc("create_time");
        Page<Emotion> emotionPage = emotionService.page(new Page<>(1, 10), emotionQueryWrapper);
        emotionPage.getRecords().forEach(emotion -> {
            emotion.setTime(TimeFormat.format(emotion.getCreateTime()));
        });
        model.addAttribute("emotionPage", emotionPage);
        //get user's friends
        List<Long> friendsId = friendshipService.getFriendIdsByUserId(Long.valueOf(id));
        model.addAttribute("isFriend", false);
        if (session.getAttribute("loginUser") != null) {
            User loginUser = (User) session.getAttribute("loginUser");
            if (friendsId.contains(loginUser.getId())) {
                model.addAttribute("isFriend", true);
            }
        }
        return "member";
    }


}
