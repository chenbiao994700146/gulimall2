package com.cb.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.cb.common.constant.AuthServerConstant;
import com.cb.common.exception.BizCodeEnume;
import com.cb.common.utils.R;
import com.cb.common.vo.MemberRespVo;
import com.cb.gulimall.auth.feign.MemberFeignService;
import com.cb.gulimall.auth.feign.ThridPartFeignService;
import com.cb.gulimall.auth.vo.UserLoginVo;
import com.cb.gulimall.auth.vo.UserRegistVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.cb.common.constant.AuthServerConstant.LOGIN_USER;

@Controller
public class LoginController {

    @Resource
    ThridPartFeignService thridPartFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Resource
    MemberFeignService memberFeignService;

    /**
     * 发送一个请求直接跳转到一个页面
     * springmvc viewcontroller; 将请求和页面映射过来
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        //TODO 接口防刷

        String redisCode = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            long s = System.currentTimeMillis() - l;
            if (System.currentTimeMillis() - l < 60000) {
                System.out.println("进入跳回信息");
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }


        String code = UUID.randomUUID().toString().substring(0, 5);
        String subString = code + "_" + System.currentTimeMillis();

        redisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone, subString, 10, TimeUnit.MINUTES);

        thridPartFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * TODO 重定向携带数据，利用session原理，将数据放在session中，只要调到下一个页面取出这个数据以后，session里面的数据就会删除
     * TODO 分布式1下的session问题
     *
     * @param vo
     * @param result
     * @param redirectAttributes
     * @return
     */
    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {

            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            //model.addAttribute("errors",errors);
            redirectAttributes.addFlashAttribute("errors", errors);
            //Request method 'POST' not supported
            //用户注册--->/regist[post]----->转发/reg.html(路径映射默认都是get访问的)
            //校验出错，转发到注册页
            // return "reg";
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //真正注册，调用远程服务
        //1.校验验证码
        String code = vo.getCode();
        String s = redisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (!StringUtils.isEmpty(s)) {
            if (code.equalsIgnoreCase(s.split("_")[0])) {
                //删除验证码；令牌机制
                redisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vo.getPhone());
                //验证通过，真正注册，调用远程服务
                R r = memberFeignService.regist(vo);
                if (r.getCode() == 0) {
                    //成功


                    return "redirect:http://auth.gulimall.com/login.html";
                } else {

                    HashMap<Object, Object> errors = new HashMap<>();
                    errors.put("code", r.getData("msg", new TypeReference<String>() {
                    }));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            } else {
                HashMap<Object, Object> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            HashMap<Object, Object> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }


    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession session) {
        Object attribute = session.getAttribute(LOGIN_USER);
        if (attribute == null) {
            //没登录
            return "login";
        } else {
            return "redirect:http://gulimall.com";
        }


    }


    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {


        R login = memberFeignService.login(vo);
        if (login.getCode() == 0) {
            MemberRespVo data = login.getData("data", new TypeReference<MemberRespVo>() {
            });
            session.setAttribute(LOGIN_USER, data);
            return "redirect:http://gulimall.com";
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>() {
            }));
            redirectAttributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }


}