package com.cb.gulimall.seckill.controller;

import com.cb.common.utils.R;
import com.cb.gulimall.seckill.service.SeckillService;
import com.cb.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

//@RestController
@Controller
public class SeckillController {

    @Resource
     SeckillService seckillService;


    /**
     * 返回当前时间可以参与的秒杀商品信息
     * @return
     */
    @ResponseBody
    @GetMapping("/currentSeckillSkus")
    public R getCurrentSeckillSkus(){

      List<SeckillSkuRedisTo> vos= seckillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }

    @ResponseBody
    @GetMapping("/sku/seckill/{skuId}")
    public R getSkuSeckillInfo(@PathVariable("skuId") Long skuId){
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SeckillSkuRedisTo to=seckillService.getSkuSeckillInfo(skuId);

        return R.ok().setData(to);
    }



    @GetMapping("/kill")
    public String secKill(@RequestParam("killId") String killId, @RequestParam("key") String key, @RequestParam("num") Integer num,
                     Model model){

       String orderSn= seckillService.kill(killId,key,num);
       model.addAttribute("orderSn",orderSn);
        return  "success";
    }
}
