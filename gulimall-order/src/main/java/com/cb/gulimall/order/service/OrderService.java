package com.cb.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cb.common.to.mq.SeckillOrderTo;
import com.cb.common.utils.PageUtils;
import com.cb.gulimall.order.entity.OrderEntity;
import com.cb.gulimall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * ¶©µ¥
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:16:15
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 订单确认页放回需要的数据
     * @return
     */
    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    PayVo getOrderPay(String orderSn);

    PageUtils queryPageWithItem(Map<String, Object> params);

    /**
     *
     * @param vo
     * @return
     */
    String handlePayResult(PayAsyncVo vo);

    void createSeck(SeckillOrderTo seckillOrderTo);

}

