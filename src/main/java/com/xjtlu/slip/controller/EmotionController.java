package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
import com.xjtlu.slip.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Enumeration;

@Controller
@Slf4j
public class EmotionController {

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/addEmotion")
    public String addEmotion(@RequestParam("emotion") String content, HttpSession session) {
        Emotion emotion = new Emotion();
        if ("".equals(content)) {
            return "redirect:/404";
        }
        emotion.setContent(content);
        User user = (User) session.getAttribute("loginUser");
        emotion.setUserId(user.getId());
        emotion.setCreateTime((System.currentTimeMillis()/1000));
        emotionService.saveOrUpdate(emotion);
        redisService.del("User:emotion:user_id:".concat(user.getId().toString()));
        return "redirect:/";
    }

    @PostMapping("/delEmotion")
    public String addEmotion(@RequestParam("id") Long id, HttpSession session, HttpServletRequest request) {
        emotionService.removeById(id);
        User loginUser = (User) session.getAttribute("loginUser");
        redisService.del("User:emotion:user_id:".concat(loginUser.getId().toString()));
        String referer = request.getHeader("Referer");
        return "redirect:"+referer;
    }

    @GetMapping("/emotion/{current}")
    public String emotion(HttpSession session, Model model,@PathVariable("current") Integer current) {
        User user = (User) session.getAttribute("loginUser");
        QueryWrapper<Emotion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", user.getId());
        queryWrapper.orderByDesc("create_time");
        Page<Emotion> page = new Page<>(current, 10);
        page = emotionService.page(page, queryWrapper);
        page.getRecords().forEach(emotion -> {
                    LocalDateTime time = LocalDateTime.ofEpochSecond(emotion.getCreateTime(), 0, ZoneOffset.of("+8"));
                    emotion.setTime(String.valueOf(time));
                });
        model.addAttribute("page", page);
        return "emotion";
    }

    @GetMapping("/emotion")
    public String emotion() {
        return "redirect:emotion/1";
    }
}
