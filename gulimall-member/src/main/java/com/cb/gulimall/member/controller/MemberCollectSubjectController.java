package com.cb.gulimall.member.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cb.gulimall.member.entity.MemberCollectSubjectEntity;
import com.cb.gulimall.member.service.MemberCollectSubjectService;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.R;


/**
 * »áÔ±ÊÕ²ØµÄ×¨Ìâ»î¶¯
 *
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@RestController
@RequestMapping("member/membercollectsubject")
public class MemberCollectSubjectController {
    @Autowired
    private MemberCollectSubjectService memberCollectSubjectService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("member:membercollectsubject:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberCollectSubjectService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("member:membercollectsubject:info")
    public R info(@PathVariable("id") Long id) {
        MemberCollectSubjectEntity memberCollectSubject = memberCollectSubjectService.getById(id);

        return R.ok().put("memberCollectSubject", memberCollectSubject);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("member:membercollectsubject:save")
    public R save(@RequestBody MemberCollectSubjectEntity memberCollectSubject) {
        memberCollectSubjectService.save(memberCollectSubject);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("member:membercollectsubject:update")
    public R update(@RequestBody MemberCollectSubjectEntity memberCollectSubject) {
        memberCollectSubjectService.updateById(memberCollectSubject);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("member:membercollectsubject:delete")
    public R delete(@RequestBody Long[] ids) {
        memberCollectSubjectService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
