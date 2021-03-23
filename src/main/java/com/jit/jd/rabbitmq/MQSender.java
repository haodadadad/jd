package com.jit.jd.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
消息发送者
 */
@Service
@Slf4j
public class MQSender {
    @Autowired
    private RabbitTemplate rabbitTemplate;



//    public void fanoutSend(Object msg){
//        log.info("发送消息：" + msg);
//        //不要路由key
//        rabbitTemplate.convertAndSend("fanoutExchange", "", msg);
//    }
//
//    public void directSend01(Object msg){
//        log.info("发送red消息：" + msg);
//        //路由key对应
//        rabbitTemplate.convertAndSend("directExchange", "queue.red", msg);
//    }
//
//    public void directSend02(Object msg){
//        log.info("发送green消息：" + msg);
//        //路由key对应
//        rabbitTemplate.convertAndSend("directExchange", "queue.green", msg);
//    }
//
//    public void topicSend01(Object msg){
//        log.info("发送消息(QUEUE01接收)：" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "queue.red.message", msg);
//    }
//
//    public void topicSend02(Object msg){
//        log.info("发送消息(QUEUE01和QUEUE02都可以接收)：" + msg);
//        rabbitTemplate.convertAndSend("topicExchange", "ke.queue.red.message", msg);
//    }
//
//    public void headersSend01(String msg){
//        log.info("发送消息(QUEUE01和QUEUE02都可以接收)：" + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color", "red");
//        properties.setHeader("speed", "fast");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headersExchange", "", message);
//    }
//
//    public void headersSend02(String msg){
//        log.info("发送消息(QUEUE01接收)：" + msg);
//        MessageProperties properties = new MessageProperties();
//        properties.setHeader("color", "red");
//        properties.setHeader("speed", "normal");
//        Message message = new Message(msg.getBytes(), properties);
//        rabbitTemplate.convertAndSend("headersExchange", "", message);
//    }
//
    /*
    发送秒杀信息
     */
    public void sendSeckillMessage(String message){
        log.info("秒杀发送消息对象：" + message);
        rabbitTemplate.convertAndSend("seckillExchange", "seckill.message", message);
    }



}
