package com.jit.jd.service;

import com.jit.jd.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.pojo.User;
import com.jit.jd.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
public interface IOrderService extends IService<Order> {
//秒杀
    Order seckill(User user, GoodsVo goods);
}
