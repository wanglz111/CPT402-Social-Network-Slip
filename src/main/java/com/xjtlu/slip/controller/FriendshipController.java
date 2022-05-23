package com.xjtlu.slip.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xjtlu.slip.pojo.Friendship;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.FriendshipService;
import com.xjtlu.slip.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class FriendshipController {
    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private RedisService redisService;

    @PostMapping("/addFriend")
    public String addFriend(@RequestParam("id") String id, HttpSession session, HttpServletRequest request){
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("error", "please log in first");
            return "redirect:/login";
        }
        Friendship friendship = new Friendship();
        User user = (User) session.getAttribute("loginUser");
        friendship.setUserId(user.getId());
        friendship.setFriendId(Long.valueOf(id));
        friendshipService.save(friendship);
        String referer = request.getHeader("Referer");
        // delete cache in redis
        redisService.del("User:friendship:user_id:".concat(String.valueOf(user.getId())));
        redisService.del("User:friendship:user_id:".concat(id));
        return "redirect:" + referer;
    }

    @PostMapping("/delFriend")
    public String addEmotion(@RequestParam("id") Long id, HttpSession session, HttpServletRequest request) {
        QueryWrapper<Friendship> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", id);
        queryWrapper.eq("friend_id", ((User) session.getAttribute("loginUser")).getId());
        queryWrapper.or().eq("user_id", ((User) session.getAttribute("loginUser")).getId()).eq("friend_id", id);
        friendshipService.remove(queryWrapper);

        User loginUser = (User) session.getAttribute("loginUser");
        redisService.del("User:friendship:user_id:".concat(String.valueOf(loginUser.getId())));
        redisService.del("User:friendship:user_id:".concat(String.valueOf(id)));

        String referer = request.getHeader("Referer");
        return "redirect:"+referer;
    }

    @GetMapping("/friends/u/{currentPage}")
    public String friends(HttpSession session, HttpServletRequest request, Model model, @PathVariable Long currentPage){
        if (session.getAttribute("loginUser") == null) {
            session.setAttribute("error", "please log in first");
            return "redirect:/login";
        }
        User user = (User) session.getAttribute("loginUser");
        IPage<User> page = friendshipService.getFriendsByUserIdPage(new Page<User>(currentPage, 10),user.getId());
        model.addAttribute("page", page);
        return "friends";
    }

}
