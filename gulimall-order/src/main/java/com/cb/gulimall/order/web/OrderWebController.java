package com.cb.gulimall.order.web;

import com.cb.common.exception.NoStockException;
import com.cb.gulimall.order.service.OrderService;
import com.cb.gulimall.order.vo.OrderConfirmVo;
import com.cb.gulimall.order.vo.OrderSubmitVo;
import com.cb.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Resource
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrande(Model model, HttpServletRequest request) throws ExecutionException, InterruptedException {

       OrderConfirmVo confirmVo= orderService.confirmOrder();

       model.addAttribute("orderConfirmData",confirmVo);

        return "confirm";
    }

    /**
     * 下单功能
     * @param vo
     * @return
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes){
        //下单：去创建订单，验令牌，验价格，锁库存
        try{
            SubmitOrderResponseVo responseVo=orderService.submitOrder(vo);
            System.out.println("订单提交的数据。。。"+vo);
            if(responseVo.getCode()==0){
                //下单成功来到支付选择页
                model.addAttribute("submitOrderResp",responseVo);
                return "pay";
            }else{
                String msg="下单失败";
                switch (responseVo.getCode()){
                    case 1:msg+="订单信息过期，请刷新再次提交"; break;
                    case 2:msg+="订单商品价格发送变化，请确认后再次提交";break;
                    case 3:msg+="库存锁定失败，商品库存不足";break;
                }
                redirectAttributes.addFlashAttribute("msg",msg);
                //下单失败回到订单确认页重新确认订单信息
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }catch (Exception e){
            if (e instanceof NoStockException){
                String message=((NoStockException) e).getMessage();
                redirectAttributes.addFlashAttribute("msg",message);
                //下单失败回到订单确认页重新确认订单信息

            }
            return "redirect:http://order.gulimall.com/toTrade";
        }



    }
}
