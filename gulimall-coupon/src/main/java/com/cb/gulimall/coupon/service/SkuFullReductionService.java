package com.cb.gulimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.to.SkuReductionTo;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.coupon.entity.SkuFullReductionEntity;

import java.util.Map;

/**
 * ÉÌÆ·Âú¼õÐÅÏ¢
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 20:56:19
 */
public interface SkuFullReductionService extends IService<SkuFullReductionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSkuReduction(SkuReductionTo skuReductionTo);

}

