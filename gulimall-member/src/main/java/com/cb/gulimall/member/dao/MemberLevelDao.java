package com.cb.gulimall.member.dao;

import com.cb.gulimall.member.entity.MemberLevelEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * »áÔ±µÈ¼¶
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:06:59
 */
@Mapper
public interface MemberLevelDao extends BaseMapper<MemberLevelEntity> {


    MemberLevelEntity getDefaultLevel();


}
