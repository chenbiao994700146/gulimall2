package com.cb.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import com.cb.gulimall.order.config.AlipayTemplate;
import com.cb.gulimall.order.service.OrderService;
import com.cb.gulimall.order.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class PayWebController {

    @Resource
    AlipayTemplate alipayTemplate;

    @Resource
    OrderService orderService;

    /**
     * 1、将支付页让浏览器展示
     * 2、支付成功后，我们要跳的用户的订单列表页
     * @param orderSn
     * @return
     * @throws AlipayApiException
     */
    @ResponseBody
    @GetMapping(value = "/payOrder",produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {

     PayVo payVo= orderService.getOrderPay(orderSn);
        //返回的是一个页面，将此页面直接交给浏览器就行
      String pay=alipayTemplate.pay(payVo);

        return pay;
    }
}
