package com.cb.gulimall.ware.feign;

import com.cb.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("gulimall-member")
public interface MemberFeginService {

    @RequestMapping("/member/memberreceiveaddress/info/{id}")
    // @RequiresPermissions("member:memberreceiveaddress:info")
     R info(@PathVariable("id") Long id);
}
