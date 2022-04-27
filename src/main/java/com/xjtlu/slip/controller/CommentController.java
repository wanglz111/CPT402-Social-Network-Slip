package com.xjtlu.slip.controller;

import com.xjtlu.slip.pojo.Comment;
import com.xjtlu.slip.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommentController {
    @Autowired
    private CommentService commentService;

    @GetMapping("/comment/{topicId}")
    public List<Comment> getComment(@PathVariable() String topicId){
        return commentService.getListByTopicId(topicId);
    }
}
