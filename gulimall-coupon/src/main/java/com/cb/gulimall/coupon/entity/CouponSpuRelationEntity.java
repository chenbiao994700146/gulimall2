package com.cb.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ÓÅ»ÝÈ¯Óë²úÆ·¹ØÁª
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 20:56:19
 */
@Data
@TableName("sms_coupon_spu_relation")
public class CouponSpuRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * ÓÅ»ÝÈ¯id
     */
    private Long couponId;
    /**
     * spu_id
     */
    private Long spuId;
    /**
     * spu_name
     */
    private String spuName;

}
