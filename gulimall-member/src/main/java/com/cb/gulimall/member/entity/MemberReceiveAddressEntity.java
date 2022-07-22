package com.cb.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * »áÔ±ÊÕ»õµØÖ·
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@Data
@TableName("ums_member_receive_address")
public class MemberReceiveAddressEntity implements Serializable {
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
     * ÊÕ»õÈËÐÕÃû
     */
    private String name;
    /**
     * µç»°
     */
    private String phone;
    /**
     * ÓÊÕþ±àÂë
     */
    private String postCode;
    /**
     * Ê¡·Ý/Ö±Ï½ÊÐ
     */
    private String province;
    /**
     * ³ÇÊÐ
     */
    private String city;
    /**
     * Çø
     */
    private String region;
    /**
     * ÏêÏ¸µØÖ·(½ÖµÀ)
     */
    private String detailAddress;
    /**
     * Ê¡ÊÐÇø´úÂë
     */
    private String areacode;
    /**
     * ÊÇ·ñÄ¬ÈÏ
     */
    private Integer defaultStatus;

}
