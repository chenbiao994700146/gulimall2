package com.cb.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ÃëÉ±ÉÌÆ·Í¨Öª¶©ÔÄ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 20:56:19
 */
@Data
@TableName("sms_seckill_sku_notice")
public class SeckillSkuNoticeEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * member_id
     */
    private Long memberId;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * »î¶¯³¡´Îid
     */
    private Long sessionId;
    /**
     * ¶©ÔÄÊ±¼ä
     */
    private Date subcribeTime;
    /**
     * ·¢ËÍÊ±¼ä
     */
    private Date sendTime;
    /**
     * Í¨Öª·½Ê½[0-¶ÌÐÅ£¬1-ÓÊ¼þ]
     */
    private Integer noticeType;

}
