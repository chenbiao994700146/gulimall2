package com.cb.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.cb.gulimall.product.vo.SkuItemSaleAttrCo;

import java.util.List;
import java.util.Map;

/**
 * skuÏúÊÛÊôÐÔ&Öµ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<SkuItemSaleAttrCo> getSaleAttrsBySpuId(Long spuId);

    List<String> getSkuSaleAttrValuesStringList(Long skuId);

}

