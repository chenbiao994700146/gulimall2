package com.cb.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cb.common.utils.HttpUtils;
import com.cb.common.utils.R;
import com.cb.common.vo.MemberRespVo;
import com.cb.gulimall.auth.feign.MemberFeignService;
import com.cb.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.cb.common.constant.AuthServerConstant.LOGIN_USER;

@Slf4j
@Controller
public class OAuth2Controller {

    @Resource
    MemberFeignService memberFeignService;

    @GetMapping("/oauth/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session, HttpServletResponse response) throws Exception {

        log.info("进入/oauth2.0/weibo/success");
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "3777541342");
        map.put("client_secret", "7461ca16fdbee91e5ba0e5cc8f4fb512");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth/weibo/success");
        map.put("code", code);

        //1.根据code换取accessToken;
        // HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", null, null, map);

//
//        if(response.getStatusLine().getStatusCode() == 200){
//            String json = EntityUtils.toString(response.getEntity());
//            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
//
//            //知道当前是那个社交用户
//            //1)、当前用户如果是第一次进网站，自动组成进来
//            //登录或者注册这个社交用户
//            R r = memberFeignService.oauth2login(socialUser);
//            if(r.getCode()==0){
//                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
//                });
//                System.out.println("登录成功：用户信息"+data);
//
//                //2.登录成功就跳回首页
//                return "redirect:http://gulimall.com";
//            }else{
//                return "redirect:http://auth.gulimall.com/login.html";
//            }
//
//        }else{
//            return "redirect:http://auth.gulimall.com/login.html";
//        }


        if (true) {
            // String json = EntityUtils.toString(response.getEntity());
            // SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            SocialUser socialUser = new SocialUser();
            socialUser.setIsRealName("chenbiao");
            socialUser.setAccess_token(UUID.randomUUID().toString());
            socialUser.setExpires_in(1000L);
            socialUser.setRemind_in("1qaz");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            socialUser.setUid(sdf.format(new Date()));
            //知道当前是那个社交用户
            //1)、当前用户如果是第一次进网站，自动组成进来
            //登录或者注册这个社交用户
            R r = memberFeignService.oauth2login(socialUser);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData("data", new TypeReference<MemberRespVo>() {
                });
                System.out.println("登录成功：用户信息" + data.toString());
                //1、第一次使用session;命令浏览器保存信息。发卡
                //2、以后浏览器访问那个网站就会带上这个网站的cookie;
                //子域之间；gulimall.com  auth.gulimall.com  order.gulimall.com
                //发卡的时候（指定父域名），即使子域系统发的卡，也能让父域之间使用。
                //TODO 1、默认发的令牌。session= 。作用域：（解决子域session共享问题）
                //TODO 2、使用JSON的序列化对数据到redis中
                session.setAttribute(LOGIN_USER, data);
                //  response.addCookie(new Cookie("JESSIONID","dada").setDomain(""));
                //2.登录成功就跳回首页
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }
}
