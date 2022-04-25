package com.xjtlu.slip.config;

import com.xjtlu.slip.interceptor.LoginInterceptor;
import com.xjtlu.slip.interceptor.RecodeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdminWebConfig implements WebMvcConfigurer {

    @Autowired
    private RecodeInterceptor recodeInterceptor;

    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(recodeInterceptor)
                .addPathPatterns("/**");
    }
}
