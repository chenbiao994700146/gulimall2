package com.cb.gulimall.member.fegin;

import com.cb.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-coupon")
public interface CouponFeingService {
    @RequestMapping("/coupon/coupon/member/list")
    public R membercoupons();
}
