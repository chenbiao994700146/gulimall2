package com.cb.gulimall.order.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * skuÐÅÏ¢
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-22 20:55:28
 */
@Data
public class SpuInfoVo implements Serializable {
    private static final long serialVersionUID = 1L;


    private Long id;

    private String spuName;

    private String spuDescription;

    private Long catalogId;

    private Long brandId;

    private BigDecimal weight;

    private Integer publishStatus;

    private Date createTime;

    private Date updateTime;

}
