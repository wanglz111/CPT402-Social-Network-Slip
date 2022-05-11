package com.xjtlu.slip.controller;

import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
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
    public String addEmotion(@RequestParam("id") Long id, HttpSession session) {
        emotionService.removeById(id);
        return "redirect:/";
    }
}
