package com.cb.gulimall.product.vo;

import com.cb.gulimall.product.entity.SkuImagesEntity;
import com.cb.gulimall.product.entity.SkuInfoEntity;
import com.cb.gulimall.product.entity.SpuInfoDescEntity;
import jdk.jfr.DataAmount;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
public class SkuItemVo {

    //1、获取sku的基本信息获取 pms_sku_info
    SkuInfoEntity info;

    boolean hasStock = true;

    //2、sku的图片信息 pms_sku_images
    List<SkuImagesEntity> images;

    //3、获取spu的销售属性组合
    List<SkuItemSaleAttrCo> saleAttr;

    //4、获取spu的介绍
    SpuInfoDescEntity desp;

    //5、获取spu的规格参数信息
    List<SpuItemAttrGroupVo> groupAttrs;


    SeckillInfoVo seckillInfo;//商品的秒杀优惠信息


}
