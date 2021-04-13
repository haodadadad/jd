package com.jit.jd.service;

import com.jit.jd.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.pojo.User;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.OrderDetailVo;
import com.jit.jd.vo.OrderVo;

import java.util.List;

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

    OrderDetailVo detail(Long orderId);
//获取秒杀地址
    String createPath(User user, Long goodsId);
//校验秒杀地址
    boolean checkPath( User user, Long goodsId,String path);
//校验验证码
    Boolean checkCaptcha(User user, Long goodsId, String captcha);

    Order payOrder(User user, Long orderId);


    List<OrderVo> findOrderVo();
}
