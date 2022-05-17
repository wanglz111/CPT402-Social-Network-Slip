package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
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
        return "redirect:/";
    }

    @PostMapping("/delEmotion")
    public String addEmotion(@RequestParam("id") Long id, HttpSession session, HttpServletRequest request) {
        emotionService.removeById(id);
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
