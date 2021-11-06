package com.haotongxue.course_service.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haotongxue.course_service.controller.UserController;
import com.haotongxue.course_service.entity.User;
import com.haotongxue.course_service.exceptionhandler.CourseException;
import com.haotongxue.course_service.service.IUserService;
import com.haotongxue.course_service.utils.JwtUtils;
import com.haotongxue.course_service.utils.UserContext;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class WeChatLoginInterceptor implements HandlerInterceptor {

    @Autowired
    IUserService userService;


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String header = request.getHeader("Authority");
        Claims claims = JwtUtils.parse(request.getHeader("Authority"));
        if (claims != null){
            String openid = claims.getSubject(); //得到用户信息
            QueryWrapper<User> tUserQueryWrapper = new QueryWrapper<>();
            tUserQueryWrapper.eq("openid",openid);
            if (userService.count()!=0){
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
