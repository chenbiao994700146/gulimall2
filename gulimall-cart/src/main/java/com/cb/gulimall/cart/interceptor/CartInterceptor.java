package com.cb.gulimall.cart.interceptor;

import com.cb.common.vo.MemberRespVo;
import com.cb.gulimall.cart.vo.UserInfoTo;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.UUID;

import static com.cb.common.constant.AuthServerConstant.LOGIN_USER;
import static com.cb.common.constant.CartConstant.TEMP_USER_COOKIE_NAME;
import static com.cb.common.constant.CartConstant.TEMP_USER_COOKIE_TIMEOUT;

/**
 * 在执行目标方法之前，判断用户的登录状态。并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    /**
     * 在目标方法执行之前
     *
     * @param request
     * @param response
     * @param handler
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {


        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo member = (MemberRespVo) session.getAttribute(LOGIN_USER);
        if (member != null) {
            //用户登录
            userInfoTo.setUserId(member.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (name.equals(TEMP_USER_COOKIE_NAME)) {
                    userInfoTo.setUserKey(cookie.getValue());
                    userInfoTo.setTemUser(true);
                }
            }
        }

        //如果没有临时用户，一定分配一个临时用户
        if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
            String s = UUID.randomUUID().toString();
            userInfoTo.setUserKey(s);
        }


        //目标方法执行之前
        threadLocal.set(userInfoTo);

        return true;
    }


    /**
     * 业务执行之后
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     */

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        UserInfoTo userInfoTo = threadLocal.get();
        if (!userInfoTo.isTemUser()) {
            Cookie cookie = new Cookie(TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }

    }
}
