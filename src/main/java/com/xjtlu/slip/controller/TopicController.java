package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.Emotion;
import com.xjtlu.slip.pojo.Topic;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.*;
import com.xjtlu.slip.utils.TimeFormat;
import com.xjtlu.slip.vo.CommentCount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/topic/d/{topicId}")
    public String topicDetails(Model model, @PathVariable String topicId, HttpSession session) {
        //获取帖子详情
        Topic topic = topicService.getById(topicId);
        topic.setLatestCommentTime(TimeFormat.format(topic.getCreateUnixTime()));
        model.addAttribute("topic", topic);
        //获取帖子作者
        Long authorId = topic.getAuthorId();
        User user = userService.getById(authorId);
        model.addAttribute("user", user);
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
            List<Emotion> emotions = emotionService.getByUserIdForIndex(loginUser.getId());
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            //获取emotion相关信息
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            Integer topicCount = topicService.getTopicCount(loginUser.getId());
            model.addAttribute("topicCount", topicCount);
            //todo 获取用户的聊天好友数
        }
        return "details";
    }

    @GetMapping("/topic/{page}")
    public String topic(Model model, @PathVariable Integer page, HttpSession session) {
        Page<Topic> topicPage = topicService.getAllTopicsAndUser(page,20);
        List<Topic> topics = topicPage.getRecords();
        //获取每条topic评论数
        Map<Long, CommentCount> topicCommentCount = topicService.getCommentCount();
        //获取最新评论
        Map<Long, Comment> latestCommentInfoEveryTopic = commentService.getLatestCommentInfoEveryTopic();
        //获取最新评论用户
        Map<Long, User> userInfo = userService.getUserMap();
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
                User user = userInfo.get(comment.getUserId());
                topic.setLatestReplyUser(user);
            }
        });
        //如果session中有用户信息,则获取用户的emotion信息
        if (session.getAttribute("loginUser") != null) {
            User user = (User) session.getAttribute("loginUser");
            List<Emotion> emotions = emotionService.getByUserIdForIndex(user.getId());
            emotions.forEach(emotion -> emotion.setTime(TimeFormat.format(emotion.getCreateTime())));
            //获取emotion相关信息
            model.addAttribute("emotions", emotions);
            model.addAttribute("emotionCount", emotions.size());
            Integer topicCount = topicService.getTopicCount(user.getId());
            model.addAttribute("topicCount", topicCount);
            //todo 获取用户的聊天好友数
        }
        model.addAttribute("topics", topics);
        return "index";
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/topic/1";
    }
}
