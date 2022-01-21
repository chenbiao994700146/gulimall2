package com.cb.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.member.entity.MemberLevelEntity;

import java.util.Map;

/**
 * »áÔ±µÈ¼¶
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

