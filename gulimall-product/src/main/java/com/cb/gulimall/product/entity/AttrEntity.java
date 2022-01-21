package com.cb.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * ÉÌÆ·ÊôÐÔ
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
@TableName("pms_attr")
public class AttrEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ÊôÐÔid
	 */
	@TableId
	private Long attrId;
	/**
	 * ÊôÐÔÃû
	 */
	private String attrName;
	/**
	 * ÊÇ·ñÐèÒª¼ìË÷[0-²»ÐèÒª£¬1-ÐèÒª]
	 */
	private Integer searchType;
	/**
	 * ÊôÐÔÍ¼±ê
	 */
	private String icon;
	/**
	 * ¿ÉÑ¡ÖµÁÐ±í[ÓÃ¶ººÅ·Ö¸ô]
	 */
	private String valueSelect;
	/**
	 * ÊôÐÔÀàÐÍ[0-ÏúÊÛÊôÐÔ£¬1-»ù±¾ÊôÐÔ£¬2-¼ÈÊÇÏúÊÛÊôÐÔÓÖÊÇ»ù±¾ÊôÐÔ]
	 */
	private Integer attrType;
	/**
	 * ÆôÓÃ×´Ì¬[0 - ½ûÓÃ£¬1 - ÆôÓÃ]
	 */
	private Long enable;
	/**
	 * ËùÊô·ÖÀà
	 */
	private Long catelogId;
	/**
	 * ¿ìËÙÕ¹Ê¾¡¾ÊÇ·ñÕ¹Ê¾ÔÚ½éÉÜÉÏ£»0-·ñ 1-ÊÇ¡¿£¬ÔÚskuÖÐÈÔÈ»¿ÉÒÔµ÷Õû
	 */
	private Integer showDesc;

}
