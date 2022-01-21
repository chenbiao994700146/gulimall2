package com.cb.gulimall.product.web;

import com.cb.gulimall.product.service.SkuInfoService;
import com.cb.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Resource
    SkuInfoService skuInfoService;

    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        System.out.println("准备查询"+skuId+"的详情");
      SkuItemVo itemVo= skuInfoService.item(skuId);
      model.addAttribute("item",itemVo);
        return "item";
    }
}
