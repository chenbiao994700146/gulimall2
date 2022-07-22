package com.cb.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * »áÔ±µÈ¼¶
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@Data
@TableName("ums_member_level")
public class MemberLevelEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * µÈ¼¶Ãû³Æ
     */
    private String name;
    /**
     * µÈ¼¶ÐèÒªµÄ³É³¤Öµ
     */
    private Integer growthPoint;
    /**
     * ÊÇ·ñÎªÄ¬ÈÏµÈ¼¶[0->²»ÊÇ£»1->ÊÇ]
     */
    private Integer defaultStatus;
    /**
     * ÃâÔË·Ñ±ê×¼
     */
    private BigDecimal freeFreightPoint;
    /**
     * Ã¿´ÎÆÀ¼Û»ñÈ¡µÄ³É³¤Öµ
     */
    private Integer commentGrowthPoint;
    /**
     * ÊÇ·ñÓÐÃâÓÊÌØÈ¨
     */
    private Integer priviledgeFreeFreight;
    /**
     * ÊÇ·ñÓÐ»áÔ±¼Û¸ñÌØÈ¨
     */
    private Integer priviledgeMemberPrice;
    /**
     * ÊÇ·ñÓÐÉúÈÕÌØÈ¨
     */
    private Integer priviledgeBirthday;
    /**
     * ±¸×¢
     */
    private String note;

}
