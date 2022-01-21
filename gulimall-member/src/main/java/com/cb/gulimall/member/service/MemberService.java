package com.cb.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.member.entity.MemberEntity;
import com.cb.gulimall.member.exception.PhoneExistException;
import com.cb.gulimall.member.exception.UserNameExistException;
import com.cb.gulimall.member.vo.MemberLoginVo;
import com.cb.gulimall.member.vo.MemberRegistVo;
import com.cb.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * »áÔ±
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(MemberRegistVo vo);

    void checkPhoneUnique(String phone) throws PhoneExistException;

    void checkUsernameUnique(String userName) throws UserNameExistException;

    MemberEntity login(MemberLoginVo vo);

    MemberEntity login(SocialUser socialUser) throws Exception;

}

