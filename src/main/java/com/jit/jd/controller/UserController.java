package com.jit.jd.controller;


import com.jit.jd.pojo.User;
import com.jit.jd.rabbitmq.MQSender;
import com.jit.jd.service.IUserService;
import com.jit.jd.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


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
    @Autowired
    private IUserService userService;
//功能描述: 用户信息(测试)
    @RequestMapping("/info")

    public String info() {
        return "userInfo";
    }

    @RequestMapping("/userInfo")
    public String userInfo(Model model,User user) {
        model.addAttribute("user", user);
        return "userInfo";
    }


    @RequestMapping("/setUserInfo")

    public String setUserInfo(Long userId, String address,String name,String phone, HttpServletRequest request, HttpServletResponse response ) {

         return userService.setUserInfo(userId,address,name,phone, request, response);


    }


}
