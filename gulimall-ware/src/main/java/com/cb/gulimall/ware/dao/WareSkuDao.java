package com.cb.gulimall.ware.dao;

import com.cb.gulimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ÉÌÆ·¿â´æ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:24:10
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {


    void addStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);

    Long getSkuStcok(@Param("skuId") Long skuId);

    List<Long> listWareIdHasSkuStock(@Param("skuId") Long skuId);

    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);

    void unlockStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("num") Integer num);
}
