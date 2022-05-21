package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xjtlu.slip.pojo.Chat;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.Friendship;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmotionService emotionService;

    @GetMapping("/chat/{userId}")
    public String chatIndex(@PathVariable Long userId, HttpSession session, Model model) {
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("error", "请先登录");
            return "redirect:/login";
        }
        User loginUser = (User) session.getAttribute("loginUser");
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("friend_id", loginUser.getId());
        queryWrapper.or().eq("user_id", loginUser.getId()).eq("friend_id", userId);
        Long pairId = 0L;
        try {
            Friendship pair = friendshipService.getOne(queryWrapper);
            pairId = pair.getPairId();
        }
        catch (Exception e) {
            return "forward:404";
        }
        QueryWrapper<Chat> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq("pair_id", pairId);
        List<Chat> chats = chatService.list(queryWrapper1);
        //判断chats长度取最后10条
        if (chats.size() > 10) {
            chats = chats.subList(chats.size() - 10, chats.size());
        } else {
            chats = chats.subList(0, chats.size());
        }
        User chatUser = userService.getById(userId);
        model.addAttribute("chatUser", chatUser);
        //获取用户最后一条emotion
        List<Emotion> usersEmotions = emotionService.getByUserId(userId);
        if (usersEmotions.size() > 0) {
            model.addAttribute("emotion", usersEmotions.get(usersEmotions.size() - 1));
        }
        model.addAttribute("chats", chats);
        model.addAttribute("pairId", pairId);
        return "chat";
    }

    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("message") String message, HttpSession session, @RequestParam("friendId") Long friendId, @RequestParam("pairId") Long pairId) {
        Chat chat = new Chat();
        chat.setContent(message);
        chat.setPairId(pairId);
        chat.setTimestamp(System.currentTimeMillis()/1000);
        chat.setReceiverUserId(friendId);
        chat.setSenderUserId(((User) session.getAttribute("loginUser")).getId());
        chatService.save(chat);
        return "redirect:/chat/" + friendId;
    }
}
