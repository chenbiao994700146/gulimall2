package com.cb.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.to.mq.OrderTo;
import com.cb.common.to.mq.StockLockedTo;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.ware.entity.WareSkuEntity;
import com.cb.gulimall.ware.vo.LockStockResult;
import com.cb.gulimall.ware.vo.SkuHasStockVo;
import com.cb.gulimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * ÉÌÆ·¿â´æ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:24:10
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void addStock(Long skuId, Long wareId, Integer skuNum);

    List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds);

    Boolean orderLockStock(WareSkuLockVo vo);


    void unlockStock(StockLockedTo to);

    void unlockStock(OrderTo orderTo);

}

