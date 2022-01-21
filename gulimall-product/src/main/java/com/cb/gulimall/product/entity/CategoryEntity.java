package com.cb.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * ÉÌÆ·Èý¼¶·ÖÀà
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
@TableName("pms_category")
public class CategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ·ÖÀàid
	 */
	@TableId
	private Long catId;
	/**
	 * ·ÖÀàÃû³Æ
	 */
	private String name;
	/**
	 * ¸¸·ÖÀàid
	 */
	private Long parentCid;
	/**
	 * ²ã¼¶
	 */
	private Integer catLevel;
	/**
	 * ÊÇ·ñÏÔÊ¾[0-²»ÏÔÊ¾£¬1ÏÔÊ¾]
	 */
	@TableLogic(value = "1",delval = "0")
	private Integer showStatus;
	/**
	 * ÅÅÐò
	 */
	private Integer sort;
	/**
	 * Í¼±êµØÖ·
	 */
	private String icon;
	/**
	 * ¼ÆÁ¿µ¥Î»
	 */
	private String productUnit;
	/**
	 * ÉÌÆ·ÊýÁ¿
	 */
	private Integer productCount;

	@JsonInclude(value = JsonInclude.Include.NON_EMPTY )
	@TableField(exist = false)
	private List<CategoryEntity> children;
}
