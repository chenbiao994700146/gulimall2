package com.cb.gulimall.seckill.to;

import com.cb.gulimall.seckill.vo.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuRedisTo {
    private Long id;
    /**
     * »î¶¯id
     */
    private Long promotionId;
    /**
     * »î¶¯³¡´Îid
     */
    private Long promotionSessionId;
    /**
     * ÉÌÆ·id
     */
    private Long skuId;


    /**
     * 商品秒杀随机码
     */
    private String randomCode;


    private BigDecimal seckillPrice;
    /**
     * ÃëÉ±×ÜÁ¿
     */
    private BigDecimal seckillCount;
    /**
     * Ã¿ÈËÏÞ¹ºÊýÁ¿
     */
    private Integer seckillLimit;
    /**
     * ÅÅÐò
     */
    private Integer seckillSort;

    //sku的详细信息
    private SkuInfoVo skuInfo;

    //当前商品秒杀的开始时间
    private Long startTime;

    //当前商品秒杀的结束时间
    private Long endTime;
}
