package com.cb.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.product.entity.CommentReplayEntity;

import java.util.Map;

/**
 * ÉÌÆ·ÆÀ¼Û»Ø¸´¹ØÏµ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
public interface CommentReplayService extends IService<CommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

