package com.jit.jd.controller;

import com.jit.jd.service.IUserService;
import com.jit.jd.vo.LoginVo;
import com.jit.jd.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/register")
public class RegisterController {
    @Autowired
    private IUserService userService;

    @RequestMapping("/toRegister")
    public String toRegister(){
        return "register";
    }



    //注册功能
    @RequestMapping("/doRegister")
    @ResponseBody
    public RespBean doRegister(@Valid Long mobile, String password, HttpServletRequest request, HttpServletResponse response) {

        return userService.doRegister(mobile,password, request, response);

    }

}
