package com.cb.gulimall.product.service.impl;

import com.cb.gulimall.product.vo.SkuItemSaleAttrCo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.product.dao.SkuSaleAttrValueDao;
import com.cb.gulimall.product.entity.SkuSaleAttrValueEntity;
import com.cb.gulimall.product.service.SkuSaleAttrValueService;


@Service("skuSaleAttrValueService")
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity> implements SkuSaleAttrValueService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuSaleAttrValueEntity> page = this.page(
                new Query<SkuSaleAttrValueEntity>().getPage(params),
                new QueryWrapper<SkuSaleAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuItemSaleAttrCo> getSaleAttrsBySpuId(Long spuId) {
        SkuSaleAttrValueDao baseMapper = this.baseMapper;
        List<SkuItemSaleAttrCo> attrCos = baseMapper.getSaleAttrsBySpuId(spuId);
        return attrCos;
    }

    @Override
    public List<String> getSkuSaleAttrValuesStringList(Long skuId) {
        SkuSaleAttrValueDao baseMapper = this.baseMapper;

        return baseMapper.getSkuSaleAttrValuesAsStringList(skuId);
    }

}