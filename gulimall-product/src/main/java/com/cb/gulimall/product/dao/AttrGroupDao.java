package com.cb.gulimall.product.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cb.gulimall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cb.gulimall.product.vo.SkuItemVo;
import com.cb.gulimall.product.vo.SpuItemAttrGroupVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ÊôÐÔ·Ö×é
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {


    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(@Param("spuId") Long spuId, @Param("catalogId") Long catalogId);

}
