package com.example.rule.template;


import com.alibaba.fastjson.JSON;
import com.example.rule.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@Configuration
public class LoginFilter implements Filter {

    @Autowired
    private RedisUtil redisUtil;


    @Bean
    public FilterRegistrationBean<LoginFilter> filterFilterRegistrationBean() {
        FilterRegistrationBean<LoginFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(this);
        bean.setName("loginFilter");
        bean.addUrlPatterns("/*");
        return bean;
    }


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 过滤掉/auth/check_api接口和login接口
        String url = request.getRequestURI();
        log.info("当前请求接口:{}", url);
        // 获取authorization
        Cookie[] cookies = request.getCookies();
        log.info("当前cookies: {}", JSON.toJSONString(cookies));
        String authorization = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    authorization = cookie.getValue();
                    break;
                }
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }


}
