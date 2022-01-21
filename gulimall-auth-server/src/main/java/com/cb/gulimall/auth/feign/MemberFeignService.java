package com.cb.gulimall.auth.feign;

import com.cb.common.utils.R;
import com.cb.gulimall.auth.vo.SocialUser;
import com.cb.gulimall.auth.vo.UserLoginVo;
import com.cb.gulimall.auth.vo.UserRegistVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("gulimall-member")
public interface MemberFeignService {

    @PostMapping("/member/member/regist")
     R regist(@RequestBody UserRegistVo vo);

    @PostMapping("/member/member/login")
     R login(@RequestBody UserLoginVo vo);

    @PostMapping("/member/member/oauth2/login")
     R oauth2login(@RequestBody SocialUser socialUser) throws Exception;
}
