package com.xjtlu.slip.interceptor;

import com.xjtlu.slip.pojo.User;
import com.xjtlu.slip.service.RedisService;
import com.xjtlu.slip.utils.CookieUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Configuration
public class LoginInterceptor implements HandlerInterceptor {
    @Resource
    private CookieUtil cookieUtil;

    @Resource
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler){
        //1. If there is user information in the session, let it go
        //2. If there is no user information in the session, judge whether there is user information in the cookie, and if so, let it go
        //3. If there is no user information in the cookie, the release will be handed over to the front-end for processing.
        HttpSession session = request.getSession();
        Object loginUser = session.getAttribute("loginUser");
        if(loginUser != null){
            return true;
        }
        if (cookieUtil.getCookie("_userSession") != null) {
            Object rawData = redisService.get("User:Session:".concat(cookieUtil.getCookie("_userSession")));
            if (rawData != null) {
                User user = (User) rawData;
                session.setAttribute("loginUser", user);
            }
        }
        return true;
    }
}
