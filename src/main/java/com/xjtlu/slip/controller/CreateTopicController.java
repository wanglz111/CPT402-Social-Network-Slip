package com.xjtlu.slip.controller;

import com.xjtlu.slip.enums.TopicEnum;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.EmotionService;
import com.xjtlu.slip.service.FriendshipService;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.service.TopicService;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.TopicType;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
@Slf4j
public class CreateTopicController {

    @Autowired
    private RedisService redisService;

    @Autowired
    private EmotionService emotionService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private FriendshipService friendshipService;


    @GetMapping("/create")
    public String createTopic(Model model, HttpSession session) {
        if (session.getAttribute("loginUser") != null) {
            User user = (User) session.getAttribute("loginUser");
            List<Emotion> emotions = null;
            Integer topicCount = 0;
            List<Long> friendships = null;
            if (redisService.get("User:emotion:user_id:".concat(String.valueOf(user.getId()))) != null) {
                try {
                    emotions = (List<Emotion>) redisService.get("User:emotion:user_id:".concat(String.valueOf(user.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取emotion信息失败，直接从数据库中获取");
                }
            } else {
                emotions = emotionService.getByUserIdForIndex(user.getId());
                redisService.set("User:emotion:user_id:".concat(String.valueOf(user.getId())), emotions);
            }

            assert emotions != null;
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            //获取emotion相关信息
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            if (redisService.get("User:topicCount:user_id:".concat(String.valueOf(user.getId()))) != null) {
                try {
                    topicCount = (Integer) redisService.get("User:topicCount:user_id:".concat(String.valueOf(user.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取topicCount信息失败，直接从数据库中获取");
                }
            } else {
                topicCount = topicService.getTopicCount(user.getId());
                redisService.set("User:topicCount:user_id:".concat(String.valueOf(user.getId())), topicCount);
            }

            model.addAttribute("topicCount", topicCount);
            //获取用户的聊天好友数
            if (redisService.get("User:friendship:user_id:".concat(String.valueOf(user.getId()))) != null) {
                try {
                    friendships = (List<Long>) redisService.get("User:friendship:user_id:".concat(String.valueOf(user.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取friendship信息失败，直接从数据库中获取");
                }
            } else {
                friendships = friendshipService.getFriendIdsByUserId(user.getId());
                redisService.set("User:friendship:user_id:".concat(String.valueOf(user.getId())), friendships);
            }
            model.addAttribute("friendships", friendships==null ? 0:friendships.size());
        }
        model.addAttribute("topicType", new TopicType());
        return "create";
    }

    @PostMapping("/createTopic")
    public String createTopic(@RequestParam("title") String title,
                              @RequestParam("content") String content,
                              @RequestParam("type") String type,
                              HttpSession session) {
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("error", "请先登录");
            return "redirect:/login";
        }
        TopicEnum topicType = TopicEnum.valueOf(type);
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setContent(content);
        topic.setType(topicType);
        topic.setAuthorId(((User) session.getAttribute("loginUser")).getId());
        topic.setAuthor(((User) session.getAttribute("loginUser")).getName());
        topic.setLatestCommentUnixTime(System.currentTimeMillis()/1000);
        topic.setCreateUnixTime(System.currentTimeMillis()/1000);
        topic.setComment(0);
        topicService.saveOrUpdate(topic);
        //删除redis缓存，强制更新
        redisService.del("User:topicCount:user_id:".concat(String.valueOf(((User) session.getAttribute("loginUser")).getId())));
        long totalPage = topicService.count() / 20;
        for (int pageNo = 1; pageNo <= totalPage; pageNo++){
            redisService.del("index:topicInfo:page:".concat(String.valueOf(pageNo)));
            redisService.del("index:AllPages:".concat(String.valueOf(pageNo)));
        }
        return "redirect:/";
    }

}
