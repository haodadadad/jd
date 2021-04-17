package com.jit.jd.controller;


import com.jit.jd.mapper.OrderMapper;
import com.jit.jd.pojo.Order;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IOrderService;
import com.jit.jd.vo.OrderDetailVo;
import com.jit.jd.vo.RespBean;
import com.jit.jd.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jit
 * @since 2021-03-18
 */
@Controller
@RequestMapping("/order")
public class OrderController  {

    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, @RequestParam("orderId") Long orderId){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detailVo = orderService.detail(orderId);
        return RespBean.success(detailVo);
    }

    @RequestMapping("/payOrder")
    @ResponseBody
    public RespBean payOrder(User user, @RequestParam("orderId") Long orderId){
        if (null == user){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

       orderService.payOrder(user, orderId);
        return RespBean.success();
    }

    //订单列表页
    @RequestMapping("/toOrderList")
    public String toList(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("orderList",orderService.findOrderVo());
        return "orderList";
    }


    //用户订单列表页
    @RequestMapping("/toUserOrderList/{userId}")
    public String toUserList(Model model, User user,@PathVariable Long userId) {
        model.addAttribute("user", user);
        model.addAttribute("userOrderList",orderService.findUserOrderVo(userId));
        return "userOrderList";
    }





    @RequestMapping("/sendOrder")

    public RespBean sendOrder( @RequestParam ("orderId") Long orderId){
        System.out.println("orderid"+orderId);
        orderService.sendOrder(orderId);
        return RespBean.success();
    }


}
