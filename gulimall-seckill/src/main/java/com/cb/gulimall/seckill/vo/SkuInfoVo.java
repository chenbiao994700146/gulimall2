package com.cb.gulimall.seckill.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SkuInfoVo {
    private Long skuId;
    /**
     * spuId
     */
    private Long spuId;
    /**
     * skuÃû³Æ
     */
    private String skuName;
    /**
     * sku½éÉÜÃèÊö
     */
    private String skuDesc;
    /**
     * ËùÊô·ÖÀàid
     */
    private Long catalogId;
    /**
     * Æ·ÅÆid
     */
    private Long brandId;
    /**
     * Ä¬ÈÏÍ¼Æ¬
     */
    private String skuDefaultImg;
    /**
     * ±êÌâ
     */
    private String skuTitle;
    /**
     * ¸±±êÌâ
     */
    private String skuSubtitle;
    /**
     * ¼Û¸ñ
     */
    private BigDecimal price;
    /**
     * ÏúÁ¿
     */
    private Long saleCount;
}
