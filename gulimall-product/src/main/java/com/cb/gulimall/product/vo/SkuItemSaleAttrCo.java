package com.cb.gulimall.product.vo;

import lombok.Data;

import java.util.List;

@Data
public class SkuItemSaleAttrCo {
    private Long attrId;
    private String attrName;
    private List<AttrValueWithSkuIdVo> attrValues;
}
