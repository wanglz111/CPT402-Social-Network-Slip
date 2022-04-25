package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.service.TopicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@Slf4j
public class TopicController {

    @Autowired
    private TopicService topicService;

    //跳转到帖子主页
    @GetMapping("/topic")
    public String topic(Model model) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("latest_comment_unix_time");
        List<Topic> topics = topicService.list(queryWrapper);
        model.addAttribute("topics", topics);
        return "topic";
    }
}
