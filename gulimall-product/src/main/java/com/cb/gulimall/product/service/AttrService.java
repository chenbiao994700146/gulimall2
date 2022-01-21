package com.cb.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.product.entity.AttrEntity;
import com.cb.gulimall.product.vo.AttrGroupRelationVo;
import com.cb.gulimall.product.vo.AttrRespVo;
import com.cb.gulimall.product.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * ÉÌÆ·ÊôÐÔ
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);


    void saveAttr(AttrVo attr);

    PageUtils querBaseAttrPage(Map<String, Object> params, Long catelogId, String type);


    AttrRespVo getAttrInfo(Long attrId);

    void updateAttr(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] vos);


    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的所有属性集合里面，挑出能被检索的属性
     * @param attrIds
     * @return
     */
    List<Long> selectSearchAttrs(List<Long> attrIds);

}

