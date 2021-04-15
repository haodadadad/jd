package com.jit.jd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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
import com.jit.jd.utils.MD5Util;
import com.jit.jd.utils.UUIDUtil;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.OrderDetailVo;
import com.jit.jd.vo.OrderVo;
import com.jit.jd.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        ValueOperations valueOperations = redisTemplate.opsForValue();

        //秒杀商品表减库存
        SeckillGoods seckillGoods =
                seckillGoodsService.getOne(new QueryWrapper<SeckillGoods>().eq("goods_id", goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        boolean result = seckillGoodsService.update(new UpdateWrapper<SeckillGoods>().setSql("stock_count = stock_count - 1").eq(
                "goods_id", goods.getId()).gt("stock_count", 0));
        if (seckillGoods.getStockCount() < 1) {
            //判断是否还有库存
            valueOperations.set("isStockEmpty:" + goods.getId(), "0");
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

    //获取秒杀地址
    @Override
    public String createPath(User user, Long goodsId) {
        //秒杀地址加密部分存redis里，和正常地址拼接
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:"+user.getId()+":"+goodsId,str,60,TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath( User user, Long goodsId,String path) {
        if (user == null || goodsId < 0 || StringUtils.isBlank(path)) {
            return false;
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断秒杀路径是否正确
        String redisPath=(String)valueOperations.get("seckillPath:"+user.getId()+":"+goodsId);
        return path.equals(redisPath);
    }
//校验验证码
    @Override
    public Boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(user == null || goodsId < 0 || StringUtils.isBlank(captcha)){
            return false;
        }
        String redisCaptcha = (String)redisTemplate.opsForValue().get("captcha:" + user.getId() + goodsId);
        return redisCaptcha.equals(captcha);
    }
//支付订单
    @Override
    public Order payOrder(User user, Long orderId) {
        Order order=orderMapper.selectById(orderId);

        order.setStatus(1);
        order.setPayDate(new Date());
        orderMapper.updateById(order);
        return order;
    }
//返回订单列表
    @Override
    public List<OrderVo> findOrderVo() {
        return orderMapper.findOrderVo();
    }

    @Override
    public Order sendOrder(Long orderId) {
        Order order=orderMapper.selectById(orderId);
        order.setStatus(2);
        orderMapper.updateById(order);
        return order;
    }
}
