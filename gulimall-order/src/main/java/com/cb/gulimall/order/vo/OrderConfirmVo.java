package com.cb.gulimall.order.vo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


public class OrderConfirmVo {

    //收货地址
    @Getter
    @Setter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Getter
    @Setter
    List<OrderItemVo> items;

    @Getter
    @Setter
    Map<Long, Boolean> stocks;

    //防重令牌
    @Getter
    @Setter
    String orderToken;

    //优惠信息
    @Getter
    @Setter
    Integer integration;

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }


    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo item : items) {
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    ;//订单总额


    public BigDecimal getPayPrice() {
        return getTotal();
    }

    ;//应付价格
}
