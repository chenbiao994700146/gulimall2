package com.cb.gulimall.coupon.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ×¨ÌâÉÌÆ·
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 20:56:19
 */
@Data
@TableName("sms_home_subject_spu")
public class HomeSubjectSpuEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * ×¨ÌâÃû×Ö
     */
    private String name;
    /**
     * ×¨Ìâid
     */
    private Long subjectId;
    /**
     * spu_id
     */
    private Long spuId;
    /**
     * ÅÅÐò
     */
    private Integer sort;

}
