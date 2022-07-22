package com.cb.gulimall.ware.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * ²Ö¿âÐÅÏ¢
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:24:10
 */
@Data
@TableName("wms_ware_info")
public class WareInfoEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * ²Ö¿âÃû
     */
    private String name;
    /**
     * ²Ö¿âµØÖ·
     */
    private String address;
    /**
     * ÇøÓò±àÂë
     */
    private String areacode;

}
