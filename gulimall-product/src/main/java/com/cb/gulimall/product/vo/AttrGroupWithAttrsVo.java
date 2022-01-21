package com.cb.gulimall.product.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.cb.gulimall.product.entity.AttrEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupWithAttrsVo {


    /**
     * ·Ö×éid
     */
    @TableId
    private Long attrGroupId;
    /**
     * ×éÃû
     */
    private String attrGroupName;
    /**
     * ÅÅÐò
     */
    private Integer sort;
    /**
     * ÃèÊö
     */
    private String descript;
    /**
     * ×éÍ¼±ê
     */
    private String icon;
    /**
     * ËùÊô·ÖÀàid
     */
    private Long catelogId;

    private List<AttrEntity> attrs;


}
