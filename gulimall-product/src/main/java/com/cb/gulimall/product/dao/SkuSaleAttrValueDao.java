package com.cb.gulimall.product.dao;

import com.cb.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cb.gulimall.product.vo.SkuItemSaleAttrCo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * skuÏúÊÛÊôÐÔ&Öµ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrCo> getSaleAttrsBySpuId(@Param("spuId") Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(@Param("skuId") Long skuId);
}
