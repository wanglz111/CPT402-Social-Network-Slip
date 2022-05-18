package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.*;
import com.xjtlu.slip.service.*;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

    @Autowired
    private FriendshipService friendshipService;

    @GetMapping("/topic/d/{topicId}")
    public String topicDetails(Model model, @PathVariable String topicId, HttpSession session) {
        //获取帖子详情
        Topic topic = topicService.getById(topicId);
        topic.setLatestCommentTime(TimeFormat.format(topic.getCreateUnixTime()));
        model.addAttribute("topic", topic);
        //获取帖子作者
        Long authorId = topic.getAuthorId();
        if (redisService.get("User:userId:" + authorId) != null) {
            User user = (User) redisService.get("User:userId:" + authorId);
            model.addAttribute("user", user);
        } else {
            User user = userService.getById(authorId);
            redisService.set("User:userId:" + authorId, user);
            model.addAttribute("user", user);
        }
        //获取帖子评论
        List<Comment> comments = commentService.getListByTopicId(topicId);
        //给帖子设置形如：1小时前，1天前，1月前，1年前的时间格式
        comments.forEach(comment -> comment.setTime(TimeFormat.format(comment.getCreateTime())));
        model.addAttribute("comments", comments);
        //获取帖子评论数
        Integer commentCount = comments.size();
        model.addAttribute("commentCount", commentCount);
        //获取帖子点击数
        Integer topicClickCount = (Integer) redisService.get("/topic/d/".concat(topicId));
        model.addAttribute("topicClickCount", topicClickCount==null ? 0:topicClickCount);
        //获取当前时间
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        model.addAttribute("localTime", ft.format(dNow));
        //获取当前用户相关emotion
        if (session.getAttribute("loginUser") != null) {
            User loginUser = (User) session.getAttribute("loginUser");
            List<Emotion> emotions = null;
            Integer topicCount = 0;
            List<Long> friendships = null;
            if (redisService.get("User:emotion:user_id:".concat(String.valueOf(loginUser.getId()))) != null) {
                try {
                    emotions = (List<Emotion>) redisService.get("User:emotion:user_id:".concat(String.valueOf(loginUser.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取emotion信息失败，直接从数据库中获取");
                }
            } else {
                emotions = emotionService.getByUserIdForIndex(loginUser.getId());
                redisService.set("User:emotion:user_id:".concat(String.valueOf(loginUser.getId())), emotions);
            }

            assert emotions != null;
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            //获取emotion相关信息
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            if (redisService.get("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId()))) != null) {
                try {
                    topicCount = (Integer) redisService.get("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取topicCount信息失败，直接从数据库中获取");
                }
            } else {
                topicCount = topicService.getTopicCount(loginUser.getId());
                redisService.set("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId())), topicCount);
            }

            model.addAttribute("topicCount", topicCount);
            //获取用户的聊天好友数
            if (redisService.get("User:friendship:user_id:".concat(String.valueOf(loginUser.getId()))) != null) {
                try {
                    friendships = (List<Long>) redisService.get("User:friendship:user_id:".concat(String.valueOf(loginUser.getId())));
                }
                catch (Exception e) {
                    log.error("redis获取friendship信息失败，直接从数据库中获取");
                }
            } else {
                friendships = friendshipService.getFriendIdsByUserId(loginUser.getId());
                redisService.set("User:friendship:user_id:".concat(String.valueOf(loginUser.getId())), friendships);
            }
            model.addAttribute("friendships", friendships==null ? 0:friendships.size());
        }
        return "details";
    }

    @GetMapping("/topic/{page}")
    public String topic(Model model, @PathVariable @DefaultValue("1") Integer page, HttpSession session) {
        List<Topic> topics = null;
        Page<Topic> topicPage = null;
        if (redisService.get("index:topicInfo:page:".concat(String.valueOf(page))) != null) {
            try {
                topics = (List<Topic>) redisService.get("index:topicInfo:page:".concat(String.valueOf(page)));
                topicPage = (Page<Topic>) redisService.get("index:AllPages:".concat(String.valueOf(page)));
            }
            catch (Exception e) {
                log.error("redis获取topic信息失败，直接从数据库中获取");
            }
        } else {
            topicPage = topicService.getAllTopicsAndUser(page,20);
            topics = topicPage.getRecords();
            //获取总页数, 为分页部分作准备
            redisService.set("index:AllPages:".concat(String.valueOf(page)), topicPage);
            //获取每条topic评论数
            Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
            //获取最新评论
            Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
            //获取最新评论用户
            Map<Long, User> userInfo = null;
            if (redisService.get("User:AllUserInfo") == null) {
                userInfo = userService.getUserMap();
                redisService.set("User:AllUserInfo", userInfo);
            } else {
                try {
                    userInfo = (Map<Long, User>) redisService.get("User:AllUserInfo");
                }
                catch (Exception e) {
                    log.error("redis获取userInfo信息失败，直接从数据库中获取");
                    userInfo = userService.getUserMap();
                    redisService.set("User:AllUserInfo", userInfo);
                }
            }
            Map<Long, User> finalUserInfo = userInfo;
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
                    User user = finalUserInfo.get(comment.getUserId());
                    topic.setLatestReplyUser(user);
                }
            });
            redisService.set("index:topicInfo:page:".concat(String.valueOf(page)), topics);
        }

        //如果session中有用户信息,则获取用户的emotion信息
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
        model.addAttribute("topics", topics);
        model.addAttribute("pages", topicPage);
        return "index";
    }

    @GetMapping("/topic/t/{typeId}")
    public String getTopicByType(@PathVariable("typeId") @DefaultValue("1") Long typeId, Model model, HttpSession session) {
        List<Topic> topics = null;
        Page<Topic> topicPage = null;

        topicPage = topicService.getAllTopicsAndUserByType(1,20, typeId);
        topics = topicPage.getRecords();
        //获取每条topic评论数
        Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
        //获取最新评论
        Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
        //获取最新评论用户
        Map<Long, User> userInfo = null;
        if (redisService.get("User:AllUserInfo") == null) {
            userInfo = userService.getUserMap();
            redisService.set("User:AllUserInfo", userInfo);
        } else {
            userInfo = (Map<Long, User>) redisService.get("User:AllUserInfo");
        }
        Map<Long, User> finalUserInfo = userInfo;
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
                User user = finalUserInfo.get(comment.getUserId());
                topic.setLatestReplyUser(user);
            }
        });

        //如果session中有用户信息,则获取用户的emotion信息
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
        model.addAttribute("topics", topics);
        model.addAttribute("pages", topicPage);
        model.addAttribute("isType", true);
        return "index";
    }

    @GetMapping("/topic/u/{currentPage}")
    public String getTopicByUserId(@PathVariable("currentPage") @DefaultValue("1") Integer  currentPage, Model model, HttpSession session) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            session.setAttribute("error", "请先登录");
            return "redirect:/login";
        }
        Page<Topic> page = topicService.getAllTopicsByUser(currentPage, 10, loginUser.getId());
        page.getRecords().forEach(topic -> topic.setLatestCommentTime(TimeFormat.format(topic.getCreateUnixTime())));
        model.addAttribute("page", page);
        return "topic-user";
    }

    @PostMapping("/delTopic")
    public String delTopic(@RequestParam("id") Long id, HttpSession session, HttpServletRequest request) {
        topicService.removeById(id);
        User user = (User) session.getAttribute("loginUser");
        String referer = request.getHeader("Referer");
        redisService.refreshTopicRecord();
        redisService.del("User:topicCount:user_id:".concat(String.valueOf(user.getId())));
        return "redirect:"+referer;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/topic/1";
    }
}
