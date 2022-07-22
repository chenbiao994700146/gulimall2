package com.cb.gulimall.auth.vo;


import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * »áÔ±
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@Data
@ToString
public class MemberRespVo implements Serializable {


    private Long id;

    private Long levelId;

    private String username;

    private String password;

    private String nickname;

    private String mobile;

    private String email;

    private String header;

    private Integer gender;

    private Date birth;

    private String city;

    private String job;

    private String sign;

    private Integer sourceType;

    private Integer integration;

    private Integer growth;

    private Integer status;

    private Date createTime;

    private String socialUid;

    private String accessToken;

    private Long expiresIn;

}
