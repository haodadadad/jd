package com.jit.jd.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jit.jd.config.AccessLimit;
import com.jit.jd.exception.GlobalException;
import com.wf.captcha.ArithmeticCaptcha;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    ??????
    ???????????? 1000 * 10  ?????????QPS:2876
    ??????QPS???3500
    ??????QPS:4486
     */

//    @RequestMapping("/doSeckill2")
//    public String doSecKill2(Model model, User user, Long goodsId) {
//        if (null == user) {
//            return "login";
//        }
//        model.addAttribute("user", user);
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if (goods == null) {
//            //???????????????
//            model.addAttribute("errmsg", RespBeanEnum.GOODS_NOT_EXISTS.getMessage());
//            return "secKillFail";
//        } else if (goods.getStockCount() < 1) {
//            //??????????????????
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
//
//        //????????????????????????
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

    //??????????????????

    //orderId  ??????  -1   ????????????  0   ?????????

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean result(User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }


    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId) {
        if (null == user) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();
        //??????????????????
        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.SECKILL_PATH_ERROR);
        }

        //????????????????????????
        SeckillOrder seckillOrder =
                (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + goodsId);
        if (seckillOrder != null) {
//            model.addAttribute("errmsg", RespBeanEnum.REPETE_STOCK.getMessage());
            return RespBean.error(RespBeanEnum.REPETE_STOCK);
        }

        //?????????????????????redis????????????
        if (EmptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        //????????????
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        //??????stock.lua????????????redis????????????
        //    Long stock = (Long) redisTemplate.execute(redisScript, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);

        if (stock < 0) {
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);


//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//
//         if (goods.getStockCount() < 1) {
//            //??????????????????
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //????????????????????????
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

    //??????????????????
    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //?????????????????????5????????????5???
//        String uri=request.getRequestURI();
//        captcha="0";
//        Integer count = (Integer)valueOperations.get(uri + ":" + user.getId());
//        if (count==null){
//            valueOperations.set(uri+":"+user.getId(),1,5,TimeUnit.SECONDS);
//        }else if(count<5){
//            valueOperations.increment(uri+":"+user.getId());
//        }else  {
//            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
//
//        }


        Boolean checkCaptcha = orderService.checkCaptcha(user, goodsId, captcha);
        if (!checkCaptcha) {
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }


    //   ????????????????????????????????????????????????redis
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



     /*
    ???????????????
     */

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (null == user) {
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        if (goodsId < 0) {
            throw new GlobalException(RespBeanEnum.GOODS_NOT_EXISTS);
        }

        //????????????????????????????????????
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cahce");
        response.setDateHeader("Expires", 0);

        //?????????????????????????????????redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
        try {
            //???????????????????????????
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????:" + e.getMessage());
        }
    }
}
