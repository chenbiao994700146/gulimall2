package com.cb.gulimall.product.feign;

import com.cb.common.utils.R;
import com.cb.gulimall.product.feign.fallback.SeckillFeignServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "gulimall-seckill",fallback = SeckillFeignServiceFallback.class)
public interface SeckillFeignService {

    @GetMapping("/sku/seckill/{skuId}")
     R getSkuSeckillInfo(@PathVariable("skuId") Long skuId);
}
