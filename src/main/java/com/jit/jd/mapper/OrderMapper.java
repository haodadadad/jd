package com.jit.jd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jit.jd.pojo.Order;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.OrderVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
public interface OrderMapper extends BaseMapper<Order> {
    //获取订单列表
    List<OrderVo> findOrderVo();
    List<OrderVo> findUserOrderVo(Long userId);
}
