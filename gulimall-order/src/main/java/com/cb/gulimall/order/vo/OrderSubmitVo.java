package com.cb.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    private Long addrId;//收货地址的id
    private Integer payType;//支付方式
    private String orderToken;//防重令牌
    private BigDecimal payPrice;//应付价格  验价
    private String note;//订单备注
    //用相关信息，直接去session取出登录的用户。
}
