package com.jit.jd.rabbitmq;


import com.jit.jd.pojo.SeckillMessage;
import com.jit.jd.pojo.SeckillOrder;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IGoodsService;
import com.jit.jd.service.IOrderService;
import com.jit.jd.service.ISeckillOrderService;
import com.jit.jd.service.IUserService;
import com.jit.jd.utils.JsonUtil;
import com.jit.jd.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {

    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;
//
//    @RabbitListener(queues = "queue_fanout01")
//    public void fanout01Receive(Object msg){
//        log.info("queue_fanout01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_fanout02")
//    public void fanout02Receive(Object msg){
//        log.info("queue_fanout02接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_direct01")
//    public void direct01Receive(Object msg){
//        log.info("queue01接收消息：" + msg);
//    }

//    @RabbitListener(queues = "queue_direct02")
//    public void direct02Receive(Object msg){
//        log.info("queue02接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic01")
//    public void topic01Receive(Object msg){
//        log.info("queue01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void topic02Receive(Object msg){
//        log.info("queue02接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "queue_header01")
//    public void headers01Receive(Message msg){
//        log.info("queue01接收Message对象：" + msg);
//        log.info("queue01接收消息:" + new String(msg.getBody()));
//    }
//
//    @RabbitListener(queues = "queue_header02")
//    public void headers02Receive(Message msg){
//        log.info("queue02接收Message对象：" + msg);
//        log.info("queue02接收消息:" + new String(msg.getBody()));
//    }
    /*
    秒杀下单操作
     */


    @RabbitListener(queues = "seckillQueue")
    public void Receive(String message){
        log.info("秒杀接收的消息对象：" + message);
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodsId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();

        //判断库存
        GoodsVo goodsVo=goodsService.findGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount()<1){
            return;
        }

        //通过redis判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + goodsId);
        if(seckillOrder != null) {
            return;
        }
       //下单
        orderService.seckill(user,goodsVo );

    }
}
