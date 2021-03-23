package com.jit.jd.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jit.jd.pojo.Order;
import com.jit.jd.pojo.SeckillMessage;
import com.jit.jd.pojo.SeckillOrder;
import com.jit.jd.pojo.User;
import com.jit.jd.rabbitmq.MQSender;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.service.IOrderService;
import com.jit.jd.service.ISeckillOrderService;
import com.jit.jd.service.IUserService;
import com.jit.jd.utils.JsonUtil;
import com.jit.jd.vo.GoodsVo;
import com.jit.jd.vo.RespBean;
import com.jit.jd.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SecKillController implements InitializingBean {
    @Autowired
    private IUserService userService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /*
    秒杀
    优化前： 1000 * 10  吞吐量QPS:2876
    缓存QPS：3500
    优化QPS:4486
     */

//    @RequestMapping("/doSeckill2")
//    public String doSecKill2(Model model, User user, Long goodsId) {
//        if (null == user) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if (goods == null) {
//            //商品不存在
//            model.addAttribute("errmsg", RespBeanEnum.GOODS_NOT_EXISTS.getMessage());
//            return "secKillFail";
//        } else if (goods.getStockCount() < 1) {
//            //商品库存不足
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//
//        //判断是否重复抢购
////        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
////                user.getId()).eq("goods_id", goodsId));
//        SeckillOrder seckillOrder=(SeckillOrder)redisTemplate.opsForValue().get("order:"+user.getId()+":"+goodsId);
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPETE_STOCK.getMessage());
//            return "secKillFail";
//        }
//
//        Order order = orderService.seckill(user, goods);
//        model.addAttribute("order", order);
//        model.addAttribute("goods", goods);
//        return "orderDetail";
//    }

    //秒杀结果查询

    //orderId  成功  -1   秒杀失败  0   排队中

       @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean result(User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }



    @RequestMapping(value = "/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //判断是否重复抢购
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPETE_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.REPETE_STOCK);
        }

        //内存标记，防止redis多余访问
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

//        //预减库存
     Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//        //使用stock.lua脚本实现redis分布式锁
   //    Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);


//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//
//         if (goods.getStockCount() < 1) {
//            //商品库存不足
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //判断是否重复抢购
//        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
//                user.getId()).eq("goods_id", goodsId));
//        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPETE_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.REPETE_STOCK);
//        }
//
//        Order order = orderService.seckill(user, goods);
//
//        return RespBean.success(order);

    }

    //   系统初始化，把商品库存数量加载到redis
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }
}
