package com.cb.gulimall.member.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * »áÔ±µÇÂ¼¼ÇÂ¼
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@Data
@TableName("ums_member_login_log")
public class MemberLoginLogEntity implements Serializable {
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
	 * ´´½¨Ê±¼ä
	 */
	private Date createTime;
	/**
	 * ip
	 */
	private String ip;
	/**
	 * city
	 */
	private String city;
	/**
	 * µÇÂ¼ÀàÐÍ[1-web£¬2-app]
	 */
	private Integer loginType;

}
