package com.jit.jd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.pojo.SeckillOrder;
import com.jit.jd.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {
//获取秒杀结果
    Long getResult(User user, Long goodsId);
}
