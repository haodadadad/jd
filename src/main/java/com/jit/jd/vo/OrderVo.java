package com.jit.jd.vo;

import com.jit.jd.pojo.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

//订单返回对象
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo extends Order {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 订单id
     */
    private Long orderId;

    /**
     * 商品id
     */
    private Long goodsId;
    /**
     * 商品图片
     */
    private String goodsImg;



}
