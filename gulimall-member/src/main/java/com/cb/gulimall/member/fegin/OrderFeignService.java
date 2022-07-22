package com.cb.gulimall.member.fegin;

import com.cb.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@FeignClient("gulimall-order")
public interface OrderFeignService {

    @PostMapping("/order/order/listWithItem")
        //@RequiresPermissions("order:order:list")
    R listWithItem(@RequestBody Map<String, Object> params);
}
