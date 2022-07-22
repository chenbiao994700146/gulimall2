package com.cb.gulimall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.cb.common.utils.R;
import com.cb.gulimall.ware.feign.MemberFeginService;
import com.cb.gulimall.ware.vo.FareVo;
import com.cb.gulimall.ware.vo.MemberAddressVo;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cb.common.utils.PageUtils;
import com.cb.common.utils.Query;

import com.cb.gulimall.ware.dao.WareInfoDao;
import com.cb.gulimall.ware.entity.WareInfoEntity;
import com.cb.gulimall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {

    @Resource
    MemberFeginService memberFeginService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");

        if (!StringUtils.isEmpty(key)) {
            wrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        R r = memberFeginService.info(addrId);
        MemberAddressVo data = r.getData("memberReceiveAddress", new TypeReference<MemberAddressVo>() {
        });
        if (data != null) {
            BigDecimal fare = new BigDecimal("8");
            fareVo.setFare(fare);
            fareVo.setAddress(data);
            return fareVo;
        }
        return null;
    }

}