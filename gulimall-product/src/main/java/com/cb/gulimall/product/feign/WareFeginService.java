package com.cb.gulimall.product.feign;

import com.cb.common.to.SkuHasStockVo;
import com.cb.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WareFeginService {

    /**
     * 1.R设计的时候加上泛型
     * <p>
     * 2.直接返回我们想要的结果
     * <p>
     * 3.自己封装解析结果
     *
     * @param skuIds
     * @return
     */
    @PostMapping("/ware/waresku/hasstock")
    public R getSkuHasStock(@RequestBody List<Long> skuIds);
}
