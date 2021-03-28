package com.jit.jd.controller;


import com.jit.jd.service.IUserService;
import com.jit.jd.vo.LoginVo;
import com.jit.jd.vo.RespBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class AdminLoginController {


    @Autowired
    private IUserService userService;

    @RequestMapping("/toAdminLogin")
    public String toLogin(){
        return "adminLogin";
    }



    //登录功能
    @RequestMapping("/doAdminLogin")
    @ResponseBody
    public RespBean doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
//        log.info("{}",loginVo);

        return userService.doLogin(loginVo,request,response);

    }

}
