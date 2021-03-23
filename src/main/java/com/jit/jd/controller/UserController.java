package com.jit.jd.controller;


import com.jit.jd.pojo.User;
import com.jit.jd.rabbitmq.MQSender;
import com.jit.jd.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jit
 * @since 2021-03-15
 */
@Controller
@RequestMapping("/user")
public class UserController  {

    @Autowired
    private MQSender mqSender;
//功能描述: 用户信息(测试)
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }




}
