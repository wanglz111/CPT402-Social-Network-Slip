package com.xjtlu.slip.utils;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;
@Component
public class CookieUtil {

    public void setCookie(String name, String value) {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getResponse();
        Cookie cookie = new Cookie(name, value);
        //set cookie for the whole domain
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Constant.COOKIE_MAX_AGE);
        //assert is idea recommend
        assert response != null;
        response.addCookie(cookie);
    }

    public String getCookie(String name) {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if (c.getName().equalsIgnoreCase(name)) {
                    return c.getValue();
                }
            }
        }
        return null;
    }

    public void clearCookie(String name) {
        HttpServletResponse response = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder
                .getRequestAttributes())).getResponse();
        //set this cookie to equal to null, and overwrite the original cookie
        Cookie cookie = new Cookie(name, null);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(-1);
        cookie.setPath("/");
        assert response != null;
        response.addCookie(cookie);
    }

}

