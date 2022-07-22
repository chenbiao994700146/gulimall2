package com.cb.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import com.cb.common.exception.BizCodeEnume;
import com.cb.gulimall.member.exception.PhoneExistException;
import com.cb.gulimall.member.exception.UserNameExistException;
import com.cb.gulimall.member.fegin.CouponFeingService;
import com.cb.gulimall.member.vo.MemberLoginVo;
import com.cb.gulimall.member.vo.MemberRegistVo;
import com.cb.gulimall.member.vo.SocialUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.cb.gulimall.member.entity.MemberEntity;
import com.cb.gulimall.member.service.MemberService;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.R;

import javax.annotation.Resource;


/**
 * »áÔ±
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @Resource
    CouponFeingService couponFeingService;


    @PostMapping("/oauth2/login")
    public R oauth2login(@RequestBody SocialUser socialUser) throws Exception {

        MemberEntity entity = memberService.login(socialUser);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD__EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_INVAILD__EXCEPTION.getMsg());
        }


    }


    @PostMapping("/login")
    public R login(@RequestBody MemberLoginVo vo) {

        MemberEntity entity = memberService.login(vo);
        if (entity != null) {
            return R.ok().setData(entity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD__EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_INVAILD__EXCEPTION.getMsg());
        }


    }


    @PostMapping("/regist")
    public R regist(@RequestBody MemberRegistVo vo) {

        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnume.PHONE_EXIST__EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST__EXCEPTION.getMsg());
        } catch (UserNameExistException e) {
            return R.error(BizCodeEnume.USER_EXIST__EXCEPTION.getCode(), BizCodeEnume.USER_EXIST__EXCEPTION.getMsg());
        }


        return R.ok();
    }


    @RequestMapping("/coupons")
    public R test() {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setNickname("张三");
        R membercoupons = couponFeingService.membercoupons();
        //membercoupons.get("coupons");
        return R.ok().put("member", memberEntity).put("coupons", membercoupons.get("coupons"));
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
