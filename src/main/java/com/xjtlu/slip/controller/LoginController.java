package com.xjtlu.slip.controller;

import cn.hutool.crypto.digest.MD5;
import com.qiniu.util.StringUtils;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.utils.*;
import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisService redisService;
//cookie controller
    @Resource
    private CookieUtil cookieUtil;

    @GetMapping("/login/check")
    public String loginCheck(Model model, String username, String password, HttpSession session, HttpServletResponse response) {
        User user;
        //if cookie exists and useful, redirect to index


        if (StringUtils.isNullOrEmpty(username)||StringUtils.isNullOrEmpty(password)) {
            model.addAttribute("msg", "username or password is null");
            return "login";
        }
        //determine if password is correct
        user = userService.getByUsernameAndPassword(username, password);
        if (user == null) {
            model.addAttribute("msg", "username or password is wrong");
            return "login";
        }
        // todo: cause frontend do this, so I comment it
//        //determine if verifyCode is correct
//        String verifyCodeSession = (String) session.getAttribute("verifyCode");
//        if (verifyCodeSession == null || !verifyCodeSession.equalsIgnoreCase(verifyCode)) {
//            model.addAttribute("msg", "verifyCode is wrong");
//            return "login";
//        }
        //remove the verifyCode from session for security
        session.removeAttribute("verifyCode");
        session.setAttribute("loginUser", user);
        //force cookie different from next time(SALT, TIME)
        String _userSession = MD5.create().digestHex(user + Constant.SALT + LocalTime.now().toString());
        //store user in redis and set cookie
        //expire time is 1 day
        redisService.set("User:Session:".concat(_userSession), user, Constant.SESSION_TIME);
        cookieUtil.setCookie("_userSession", _userSession);

        return "redirect:/";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        //clear the session
        session.invalidate();
        //clear the cookie if it exists
        redisService.del("User:Session:".concat(cookieUtil.getCookie("_userSession")));
        cookieUtil.clearCookie("_userSession");
        return "goodbye";
    }

    @GetMapping("/login")
    public String login(Model model, String username, String password, HttpSession session, HttpServletResponse response) {
        if (cookieUtil.getCookie("_userSession") != null) {
            Object rawData = redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
            if (rawData != null) {
                User user = (User) rawData;
                session.setAttribute("loginUser", user);
                return "redirect:/";
            }
        }
        return "login";
    }


}


