package com.cb.gulimall.seckill.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SeckillSessionWithSkus {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * ³¡´ÎÃû³Æ
     */
    private String name;
    /**
     * Ã¿ÈÕ¿ªÊ¼Ê±¼ä
     */
    private Date startTime;
    /**
     * Ã¿ÈÕ½áÊøÊ±¼ä
     */
    private Date endTime;
    /**
     * ÆôÓÃ×´Ì¬
     */
    private Integer status;
    /**
     * ´´½¨Ê±¼ä
     */
    private Date createTime;

    private List<SeckillSkuVo> relationSkus;

}
