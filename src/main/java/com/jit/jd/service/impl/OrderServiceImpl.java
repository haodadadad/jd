package com.jit.jd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jit.jd.exception.GlobalException;
import com.jit.jd.mapper.GoodsMapper;
import com.jit.jd.mapper.OrderMapper;
import com.jit.jd.pojo.Order;
import com.jit.jd.pojo.SeckillGoods;
import com.jit.jd.pojo.SeckillOrder;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IOrderService;
import com.jit.jd.service.ISeckillGoodsService;
import com.jit.jd.service.ISeckillOrderService;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.OrderDetailVo;
import com.jit.jd.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    //秒杀
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations=redisTemplate.opsForValue();

        //秒杀商品表减库存
        SeckillGoods seckillGoods =
                seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq(
                "goods_id", goods.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount()<1){
            //判断是否还有库存
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");
            return null;

        }

//        if (!result) {
//            throw new GlobalException(RespBeanEnum.SECKILL_FAILED);
//        }

        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrderService.save(seckillOrder);

        //秒杀订单信息存redis里
        redisTemplate.opsForValue().

                set("order:" + user.getId() + goods.getId(), seckillOrder);

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXISTS);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsMapper.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setGoodsVo(goodsVo);
        detailVo.setOrder(order);
        return detailVo;
    }
}
