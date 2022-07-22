package com.cb.gulimall.product.dao;

import com.cb.gulimall.product.entity.CategoryBrandRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Æ·ÅÆ·ÖÀà¹ØÁª
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Mapper
public interface CategoryBrandRelationDao extends BaseMapper<CategoryBrandRelationEntity> {

    void updateCategory(@Param("catId") Long catId, @Param("name") String name);
}
