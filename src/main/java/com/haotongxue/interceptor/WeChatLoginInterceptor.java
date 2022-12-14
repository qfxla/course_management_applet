package com.haotongxue.interceptor;

import com.haotongxue.cacheUtil.LoadingRedisCache;
import com.haotongxue.exceptionhandler.CourseException;
import com.haotongxue.service.IUserService;
import com.haotongxue.utils.JwtUtils;
import com.haotongxue.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WeChatLoginInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;

    @Resource(name = "loginCache")
    LoadingRedisCache cache;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Claims claims = JwtUtils.parse(request.getHeader("Authority"));
        if (claims != null){
            String openid = claims.getSubject(); //得到用户信息
            Object o = cache.get(openid);
            if (o != null){
                UserContext.add(openid);
                return true;
            }
        }
        CourseException courseException = new CourseException();
        courseException.setCode(20001);
        courseException.setMsg("请先登录验证");
        throw courseException;
    }
}
