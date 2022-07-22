package com.cb.gulimall.product.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ÊôÐÔ·Ö×é
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @TableField(exist = false)
    private Long[] catelogPath;

}
