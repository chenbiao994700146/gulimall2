package com.cb.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.product.entity.BrandEntity;
import com.cb.gulimall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * Æ·ÅÆ·ÖÀà¹ØÁª
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDatail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrand(Long brandId, String name);


    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandsBtCatId(Long catId);

}

