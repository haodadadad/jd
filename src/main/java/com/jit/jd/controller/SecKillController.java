package com.jit.jd.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jit.jd.pojo.Order;
import com.jit.jd.pojo.SeckillOrder;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.service.IOrderService;
import com.jit.jd.service.ISeckillOrderService;
import com.jit.jd.service.IUserService;
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
public class SecKillController  {
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
//    @Autowired
//    private MQSender mqSender;
//    @Autowired
//    private RedisScript<Long> script;

//    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    /*
    秒杀
    优化前： 1000 * 10  吞吐量QPS:2876
    缓存QPS：3500
    优化QPS:4486
     */

    @RequestMapping("/doSeckill")
    public String doSecKill(Model model, User user,  long goodsId){
        if(null == user){
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goods == null){
            //商品不存在
            model.addAttribute("errmsg", RespBeanEnum.GOODS_NOT_EXISTS.getMessage());
            return "secKillFail";
        }else if(goods.getStockCount() < 1){
            //商品库存不足
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }

        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id",
                user.getId()).eq("goods_id", goodsId));
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPETE_STOCK.getMessage());
            return "secKillFail";
        }

        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }

//    @RequestMapping(value = "/{path}/doSecKill", method = RequestMethod.POST)
//    @ResponseBody
//    public RespBean doSecKill(User user, long goodsId, @PathVariable("path") String path) {
//        if (null == user) {
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//
//        //秒杀地址检查
//        Boolean checkPath = orderService.checkPath(path, user, goodsId);
//        if(!checkPath) {
//            return RespBean.error(RespBeanEnum.SECKILL_PATH_ERROR);
//        }
//
//        ValueOperations valueOperations = redisTemplate.opsForValue();
//        //判断是否重复抢购, 更改为通过redis去取，由uid+goodsid确定同一用户不能抢购两单
//        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + goodsId);
//        if(seckillOrder != null){
//            return RespBean.error(RespBeanEnum.REPETE_STOCK);
//        }
//
//        //内存标记，防止redis多余访问
//        if(EmptyStockMap.get(goodsId)){
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //库存预减
//        //decrement:递减，原子型
////        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
//
//        //使用stock.lua脚本实现redis分布式锁
//        Long stock = (Long)redisTemplate.execute(script, Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
//        if(stock < 0){
//            //当为-1时让他为0，更好看
//            valueOperations.increment("seckillGoods:" + goodsId);
//            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
//        }
//
//        //rabbitmq发送消息对象
//        SeckillMessage message = new SeckillMessage(user, goodsId);
//        mqSender.sendSeckillMessage(JsonUtil.object2JsonStr(message));
//        return RespBean.success(0);
//
//    }
//
//    /*
//    系统初始化，把商品库存数量加载到redis
//     */
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        List<GoodsVo> list = goodsService.findGoodsVo();
//        if(CollectionUtils.isEmpty(list)){
//            return;
//        }
//        list.forEach(goodsVo -> {
//            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
//            EmptyStockMap.put(goodsVo.getId(), false);
//        });
//
//    }
//
//    /*
//    秒杀结果查询
//     */
//    @RequestMapping(value = "/result", method = RequestMethod.GET)
//    @ResponseBody
//    public RespBean result(User user, Long goodsId){
//        if(null == user){
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//
//        Long orderId = seckillOrderService.getResult(user, goodsId);
//        return RespBean.success(orderId);
//    }
//
//    /*
//    获取秒杀地址
//     */
//    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
//    @RequestMapping(value="/path", method = RequestMethod.GET)
//    @ResponseBody
//    public RespBean path(User user, Long goodsId, String captcha, HttpServletRequest request){
//        if(null == user){
//            return RespBean.error(RespBeanEnum.SESSION_ERROR);
//        }
//
////        //采用计数器方法，限制访问次数，5秒内访问5次
////        ValueOperations valueOperations = redisTemplate.opsForValue();
////        String uri = request.getRequestURI();
////        Integer count = (Integer) valueOperations.get(uri + ":" + user.getId());
////        if(count == null){
////            valueOperations.set(uri + ":" + user.getId(),1,  5, TimeUnit.SECONDS);
////        }else if(count < 5){
////            valueOperations.increment(uri + ":" + user.getId());
////        }else{
////            return RespBean.error(RespBeanEnum.ACCESS_LIMIT_REAHCED);
////        }
//
//        //验证码检查
//        Boolean checkCaptcha = orderService.checkCaptcha(user, goodsId, captcha);
//        if(!checkCaptcha){
//            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
//        }
//
//        String path = orderService.createPath(user, goodsId);
//        return RespBean.success(path);
//    }
//
//    /*
//    获取验证码
//     */
//
//    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
//    public void verifyCode(User user, Long goodsId, HttpServletResponse response){
//        if(null == user){
//            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
//        }
//        if(goodsId < 0){
//            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
//        }
//
//        //设置请求头为输出图片类型
//        response.setContentType("image/jpg");
//        response.setHeader("Pargam", "No-cache");
//        response.setHeader("Cache-Control", "no-cahce");
//        response.setDateHeader("Expires", 0);
//
//        //生成验证码，将结果放入redis
//        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
//        redisTemplate.opsForValue().set("captcha:" + user.getId() + goodsId, captcha.text(), 300, TimeUnit.SECONDS);
//        try{
//            //验证码以流形式输出
//            captcha.out(response.getOutputStream());
//        }catch (IOException e){
//            log.error("验证码生成失败:" + e.getMessage());
//        }
//    }
}
