package com.xjtlu.slip.interceptor;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Configuration
public class RecodeInterceptor implements HandlerInterceptor {
    // inject the actual template
    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public void afterCompletion(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler, Exception ex) throws Exception {
        // for every request, we set a record in redis.
        String URI = request.getRequestURI();
        if (URI.contains("jsessionid")) {
            URI = URI.split(";")[0];
        }
        redisTemplate.opsForValue().increment(URI);
    }
}
