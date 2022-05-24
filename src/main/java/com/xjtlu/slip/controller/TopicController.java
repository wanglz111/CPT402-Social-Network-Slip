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
        // Get topic details
        Topic topic = topicService.getById(topicId);
        topic.setLatestCommentTime(TimeFormat.format(topic.getCreateUnixTime()));
        model.addAttribute("topic", topic);
        // Get topic author
        Long authorId = topic.getAuthorId();
        if (redisService.get("User:userId:" + authorId) != null) {
            User user = (User) redisService.get("User:userId:" + authorId);
            model.addAttribute("user", user);
        } else {
            User user = userService.getById(authorId);
            redisService.set("User:userId:" + authorId, user);
            model.addAttribute("user", user);
        }
        // Get topic comments
        List<Comment> comments = commentService.getListByTopicId(topicId);
        // Set the time format for the post as: 1 hour ago, 1 day ago, 1 month ago, 1 year ago
        comments.forEach(comment -> comment.setTime(TimeFormat.format(comment.getCreateTime())));
        model.addAttribute("comments", comments);
        // Get the number of comments on a post
        Integer commentCount = comments.size();
        model.addAttribute("commentCount", commentCount);
        // Get post clicks
        Integer topicClickCount = (Integer) redisService.get("/topic/d/".concat(topicId));
        model.addAttribute("topicClickCount", topicClickCount==null ? 0:topicClickCount);
        // get current time
        Date dNow = new Date( );
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        model.addAttribute("localTime", ft.format(dNow));
        // Get current user related emotions
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
                    log.error("Redis fails to get emotion information, and gets it directly from the database");
                }
            } else {
                emotions = emotionService.getByUserIdForIndex(loginUser.getId());
                redisService.set("User:emotion:user_id:".concat(String.valueOf(loginUser.getId())), emotions);
            }

            assert emotions != null;
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            // Get emotion related information
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            if (redisService.get("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId()))) != null) {
                try {
                    topicCount = (Integer) redisService.get("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId())));
                }
                catch (Exception e) {
                    log.error("Redis fails to obtain topic Count information, and it is obtained directly from the database");
                }
            } else {
                topicCount = topicService.getTopicCount(loginUser.getId());
                redisService.set("User:topicCount:user_id:".concat(String.valueOf(loginUser.getId())), topicCount);
            }

            model.addAttribute("topicCount", topicCount);
            //Get the number of chat friends of the user
            if (redisService.get("User:friendship:user_id:".concat(String.valueOf(loginUser.getId()))) != null) {
                try {
                    friendships = (List<Long>) redisService.get("User:friendship:user_id:".concat(String.valueOf(loginUser.getId())));
                }
                catch (Exception e) {
                    log.error("Redis failed to get friendship information, get it directly from the database");
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
        if (redisService.get("index:topicInfo:page:".concat(String.valueOf(page))) != null && redisService.get("index:AllPages:".concat(String.valueOf(page))) != null) {
            try {
                topics = (List<Topic>) redisService.get("index:topicInfo:page:".concat(String.valueOf(page)));
                topicPage = (Page<Topic>) redisService.get("index:AllPages:".concat(String.valueOf(page)));
            }
            catch (Exception e) {
                log.error("Redis failed to obtain topic information, directly obtained from the database");
            }
        } else {
            topicPage = topicService.getAllTopicsAndUser(page,20);
            topics = topicPage.getRecords();
            // Get the total number of pages, prepare for the pagination section
            redisService.set("index:AllPages:".concat(String.valueOf(page)), topicPage,60*60);
            // Get the number of comments per topic
            Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
            // Get the latest reviews
            Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
            // Get latest comments from users
            Map<Long, User> userInfo = null;
            if (redisService.get("User:AllUserInfo") == null) {
                userInfo = userService.getUserMap();
                redisService.set("User:AllUserInfo", userInfo);
            } else {
                try {
                    userInfo = (Map<Long, User>) redisService.get("User:AllUserInfo");
                }
                catch (Exception e) {
                    log.error("Redis failed to get user Info information, get it directly from the database");
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
            redisService.set("index:topicInfo:page:".concat(String.valueOf(page)), topics, 60*60);
        }

        // If there is user information in the session, get the user's emotion information
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
                    log.error("Redis fails to get emotion information, and gets it directly from the database");
                }
            } else {
                emotions = emotionService.getByUserIdForIndex(user.getId());
                redisService.set("User:emotion:user_id:".concat(String.valueOf(user.getId())), emotions);
            }

            assert emotions != null;
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            // Get emotion related information
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            if (redisService.get("User:topicCount:user_id:".concat(String.valueOf(user.getId()))) != null) {
                try {
                    topicCount = (Integer) redisService.get("User:topicCount:user_id:".concat(String.valueOf(user.getId())));
                }
                catch (Exception e) {
                    log.error("Redis fails to obtain topic Count information, and it is obtained directly from the database");
                }
            } else {
                topicCount = topicService.getTopicCount(user.getId());
                redisService.set("User:topicCount:user_id:".concat(String.valueOf(user.getId())), topicCount);
            }

            model.addAttribute("topicCount", topicCount);
            //Get the number of chat friends of the user
            if (redisService.get("User:friendship:user_id:".concat(String.valueOf(user.getId()))) != null) {
                try {
                    friendships = (List<Long>) redisService.get("User:friendship:user_id:".concat(String.valueOf(user.getId())));
                }
                catch (Exception e) {
                    log.error("Redis failed to get friendship information, get it directly from the database");
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
        // Get the number of comments per topic
        Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
        // Get the latest reviews
        Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
        // Get latest comments from users
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

        // If there is user information in the session, get the user's emotion information
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
            session.setAttribute("error", "please log in first");
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
