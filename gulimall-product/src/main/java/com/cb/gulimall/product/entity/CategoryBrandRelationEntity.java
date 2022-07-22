package com.cb.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * Æ·ÅÆ·ÖÀà¹ØÁª
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
@TableName("pms_category_brand_relation")
public class CategoryBrandRelationEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId
    private Long id;
    /**
     * Æ·ÅÆid
     */
    private Long brandId;
    /**
     * ·ÖÀàid
     */
    private Long catelogId;
    /**
     *
     */
    private String brandName;
    /**
     *
     */
    private String catelogName;

}
