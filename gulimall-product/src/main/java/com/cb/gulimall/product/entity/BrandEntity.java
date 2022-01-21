package com.cb.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import com.cb.common.valid.AddGroup;
import com.cb.common.valid.ListValue;
import com.cb.common.valid.UpdateGroup;
import com.cb.common.valid.UpdateStatusGroup;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;

/**
 * Æ·ÅÆ
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * Æ·ÅÆid
	 */
	@NotNull(message = "修改必须指定品牌id",groups = {UpdateGroup.class})
	@Null(message = "新增不能指定id",groups = {AddGroup.class})
	@TableId
	private Long brandId;
	/**
	 * Æ·ÅÆÃû
	 */
	@NotBlank(message = "品牌名必须提交",groups = {AddGroup.class,UpdateGroup.class})
	private String name;
	/**
	 * Æ·ÅÆlogoµØÖ·
	 */
	@NotEmpty(groups = {AddGroup.class})
	@URL(message = "logo必须是一个合法的url地址",groups = {AddGroup.class,UpdateGroup.class})
	private String logo;
	/**
	 * ½éÉÜ
	 */
	@NotEmpty(groups = {AddGroup.class})
	@Pattern(regexp = "/^[a-zA-Z]$/",message = "检索首字母必须是一个字母")
	private String descript;
	/**
	 * ÏÔÊ¾×´Ì¬[0-²»ÏÔÊ¾£»1-ÏÔÊ¾]
	 */
	@NotNull(groups = {AddGroup.class,UpdateStatusGroup.class})
	@ListValue(vals={0,1},groups = {AddGroup.class, UpdateStatusGroup.class})
	private Integer showStatus;
	/**
	 * ¼ìË÷Ê××ÖÄ¸
	 */
	private String firstLetter;
	/**
	 * ÅÅÐò
	 */
	@NotNull(groups = {AddGroup.class})
	@Min(value = 0,message = "排序大于等于0",groups = {AddGroup.class,UpdateGroup.class})
	private Integer sort;

}
