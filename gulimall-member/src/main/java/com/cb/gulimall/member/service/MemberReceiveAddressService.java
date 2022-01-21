package com.cb.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.member.entity.MemberReceiveAddressEntity;

import java.util.List;
import java.util.Map;

/**
 * »áÔ±ÊÕ»õµØÖ·
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
public interface MemberReceiveAddressService extends IService<MemberReceiveAddressEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<MemberReceiveAddressEntity> getAddress(Long memberId);

}

