package com.cb.common.to.mq;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class StockDetailTo {

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * ¹ºÂò¸öÊý
     */
    private Integer skuNum;
    /**
     * ¹¤×÷µ¥id
     */
    private Long taskId;

    private Long wareId;

    private Integer lockStatus;
}
