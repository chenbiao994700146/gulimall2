package com.cb.gulimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.ware.vo.MergeVo;
import com.cb.gulimall.ware.entity.PurchaseEntity;
import com.cb.gulimall.ware.vo.PurchaseDoneVo;
import com.cb.gulimall.ware.vo.PurchaseItemDoneVo;

import java.util.List;
import java.util.Map;

/**
 * ²É¹ºÐÅÏ¢
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:24:10
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageUnreceive(Map<String, Object> params);

    void mergePurchase(MergeVo mergeVo);



    void received(List<Long> ids);


    void done(PurchaseDoneVo doneVo);

}

