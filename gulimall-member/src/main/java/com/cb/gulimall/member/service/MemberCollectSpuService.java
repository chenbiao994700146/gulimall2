package com.cb.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.member.entity.MemberCollectSpuEntity;

import java.util.Map;

/**
 * »áÔ±ÊÕ²ØµÄÉÌÆ·
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

