package com.jit.jd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jit.jd.pojo.User;
import com.jit.jd.vo.LoginVo;
import com.jit.jd.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jit
 * @since 2021-03-15
 */
public interface IUserService extends IService<User> {


    //登录
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);


//跟据cookie获取用户
    User getUserByCookie(String userTicket,HttpServletRequest request ,HttpServletResponse response);

// 更新密码

    RespBean updatePassword(String userTicket, String password, HttpServletRequest request,
                            HttpServletResponse response);

}

