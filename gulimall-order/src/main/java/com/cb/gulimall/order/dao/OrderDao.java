package com.cb.gulimall.order.dao;

import com.cb.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * ¶©µ¥
 * 
 * @author chenbiao
 * @email chenbiao@gmail.com
 * @date 2021-09-23 21:16:15
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

    void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
