package com.cb.gulimall.member.web;

import com.cb.common.utils.R;
import com.cb.gulimall.member.fegin.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;

@Controller
public class MemberWebController {

    @Resource
    OrderFeignService orderFeignService;

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                  Model model) {
        //查出当前登录的用户的所有订单列表数据
        HashMap<String, Object> page = new HashMap<>();
        page.put("page", pageNum.toString());

        //
        R r = orderFeignService.listWithItem(page);
        model.addAttribute("orders", r);
        orderFeignService.listWithItem(page);

        return "orderList";
    }
}
