package com.cb.gulimall.product.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillInfoVo {
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
    private BigDecimal seckillLimit;
    /**
     * ÅÅÐò
     */
    private Integer seckillSort;

    //当前商品秒杀的开始时间
    private Long startTime;

    //当前商品秒杀的结束时间
    private Long endTime;
}
