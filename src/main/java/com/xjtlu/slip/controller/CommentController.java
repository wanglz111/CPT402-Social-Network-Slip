package com.xjtlu.slip.controller;

import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.CommentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Slf4j
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/comment/{topicId}")
    public List<Comment> getComment(@PathVariable() String topicId){
        return commentService.getListByTopicId(topicId);
    }

    @PostMapping("/addComment")
    public String addComment(@RequestParam("topicId") String topicId, @RequestParam("replyContent") String content, @RequestParam("userId") String replyUserId, HttpSession session){
        User loginUser = (User) session.getAttribute("loginUser");
        // 判断用户与评论的用户是否一致，防止post攻击
        assert loginUser.getId().toString().equals(replyUserId);
        log.info("topicId:"+topicId+" content:"+content+" replyUserId:"+replyUserId);
        return "redirect:/topic/d/"+topicId;
    }
}
