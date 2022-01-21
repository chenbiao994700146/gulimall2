package com.cb.gulimall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.cb.common.to.es.SkuEsModel;
import com.cb.common.utils.R;
import com.cb.gulimall.product.entity.SkuImagesEntity;
import com.cb.gulimall.product.entity.SpuInfoDescEntity;
import com.cb.gulimall.product.feign.SeckillFeignService;
import com.cb.gulimall.product.service.*;
import com.cb.gulimall.product.vo.SeckillInfoVo;
import com.cb.gulimall.product.vo.SkuItemSaleAttrCo;
import com.cb.gulimall.product.vo.SkuItemVo;
import com.cb.gulimall.product.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.product.dao.SkuInfoDao;
import com.cb.gulimall.product.entity.SkuInfoEntity;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Resource
    SkuImagesService skuImagesService;

    @Resource
    SpuInfoDescService spuInfoDescService;

    @Resource
    AttrGroupService attrGroupService;

    @Resource
    SkuSaleAttrValueService skuSaleAttrValueService;

    @Resource
    SeckillFeignService seckillFeignService;

    @Resource
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSkuInfo(SkuInfoEntity skuInfoEntity) {
        this.baseMapper.insert(skuInfoEntity);
    }

    @Override
    public PageUtils queryPageByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        /**
         *
         * key:
         * catelogId: 0
         * brandId: 0
         * min: 0
         * max: 0
         */
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("sku_id",key).or().like("sku_name",key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if(!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)){

            wrapper.eq("catalog_id",catelogId);
        }
        String brandId = (String) params.get("brandId");

        if(!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)){
            wrapper.eq("brand_id",brandId);
        }
        String min = (String) params.get("min");
        if(!StringUtils.isEmpty(min)){
            wrapper.ge("price",min);
        }
        String max = (String) params.get("max");

        if(!StringUtils.isEmpty(max) ){
            try {
                BigDecimal bigDecimal = new BigDecimal(max);
               if( bigDecimal.compareTo(new BigDecimal("0"))==1){
                   wrapper.le("price",max);
               }

            }catch (Exception e){

            }

        }

        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        List<SkuInfoEntity> list = this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id",spuId));

        return list;
    }

    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        //new一个线程池，且有返回值
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            //1、获取sku的基本信息获取 pms_sku_info
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);

        //异步线程，且接受前面的返回值，自己无返回
        CompletableFuture<Void> attrFutrue = infoFuture.thenAcceptAsync((res) -> {
            //3、获取spu的销售属性组合
            List<SkuItemSaleAttrCo> saleAttrCos = skuSaleAttrValueService.getSaleAttrsBySpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrCos);
        }, executor);


        CompletableFuture<Void> descFuture = infoFuture.thenAcceptAsync((res) -> {
            //4、获取spu的介绍

            SpuInfoDescEntity spuInfoDescEntity = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesp(spuInfoDescEntity);
        }, executor);


        CompletableFuture<Void> baseAttrFuture = infoFuture.thenAcceptAsync((res) -> {
            //5、获取spu的规格参数信息
            List<SpuItemAttrGroupVo> attrGroupVos = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroupVos);
        }, executor);


        //无法回结果的异步任务
        CompletableFuture<Void> imageFuture = CompletableFuture.runAsync(() -> {
            //2、sku的图片信息 pms_sku_images
            List<SkuImagesEntity> images = skuImagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);


        //3、查询当前sku是否参与秒杀优惠
        CompletableFuture<Void> seckill = CompletableFuture.runAsync(() -> {
            R seckillInfo = seckillFeignService.getSkuSeckillInfo(skuId);
            if(seckillInfo.getCode()==0){
                SeckillInfoVo seckillInfoVo = seckillInfo.getData(new TypeReference<SeckillInfoVo>() {
                });
                skuItemVo.setSeckillInfo(seckillInfoVo);
            }
        }, executor);

        //等待所有异步任务执行完成返回结果
      CompletableFuture.allOf( attrFutrue, descFuture, baseAttrFuture, imageFuture,seckill).get();

      //总结：

        return skuItemVo;
    }


}