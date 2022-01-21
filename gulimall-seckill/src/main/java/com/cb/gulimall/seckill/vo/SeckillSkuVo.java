package com.cb.gulimall.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SeckillSkuVo {
    /**
     * id
     */

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
     * ÃëÉ±¼Û¸ñ
     */
    private BigDecimal seckillPrice;
    /**
     * ÃëÉ±×ÜÁ¿
     */
    private Integer seckillCount;
    /**
     * Ã¿ÈËÏÞ¹ºÊýÁ¿
     */
    private BigDecimal seckillLimit;
    /**
     * ÅÅÐò
     */
    private Integer seckillSort;

}
