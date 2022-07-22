package com.cb.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ÉÌÆ·½×ÌÝ¼Û¸ñ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 20:56:19
 */
@Data
@TableName("sms_sku_ladder")
public class SkuLadderEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * spu_id
     */
    private Long skuId;
    /**
     * Âú¼¸¼þ
     */
    private Integer fullCount;
    /**
     * ´ò¼¸ÕÛ
     */
    private BigDecimal discount;
    /**
     * ÕÛºó¼Û
     */
    private BigDecimal price;
    /**
     * ÊÇ·ñµþ¼ÓÆäËûÓÅ»Ý[0-²»¿Éµþ¼Ó£¬1-¿Éµþ¼Ó]
     */
    private Integer addOther;

}
