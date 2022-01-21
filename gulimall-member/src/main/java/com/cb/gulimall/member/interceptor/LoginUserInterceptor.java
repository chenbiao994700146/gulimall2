package com.cb.gulimall.member.interceptor;

import com.cb.common.constant.AuthServerConstant;
import com.cb.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

   public static  ThreadLocal<MemberRespVo> loginUser=new ThreadLocal<>();


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        ///member/memberreceiveaddress/info/{id}
        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if(match){
            return true;
        }

        MemberRespVo attribute = (MemberRespVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
        if(attribute!=null){
            loginUser.set(attribute);
            return true;
        }else{
            //没登录去登录
            request.getSession().setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.gulimall.com/login.html");
            return false;
        }



    }
}
