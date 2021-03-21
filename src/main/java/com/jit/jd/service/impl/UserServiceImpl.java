package com.jit.jd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jit.jd.exception.GlobalException;
import com.jit.jd.mapper.UserMapper;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IUserService;
import com.jit.jd.utils.CookieUtil;
import com.jit.jd.utils.MD5Util;
import com.jit.jd.utils.UUIDUtil;
import com.jit.jd.vo.LoginVo;
import com.jit.jd.vo.RespBean;
import com.jit.jd.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.RedisTemplate;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jit
 * @since 2021-03-15
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    //登录
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //参数校验
//        if (StringUtils.isEmpty(mobile)||StringUtils.isEmpty(password)){
//
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//
//        }
//        if (!ValidatorUtil.isMobile(mobile)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //跟据手机号获取用户
        User user = userMapper.selectById(mobile);
        if (null == user) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //判断密码是否正确
        if (!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

//        //生成cookie
        String ticket = UUIDUtil.uuid();
        //将用户信息存入redis
        redisTemplate.opsForValue().set("user:" + ticket, user);
//        request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success();
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(userTicket)) {
            return null;
        }

        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);

        if (user != null) {
            CookieUtil.setCookie(request, response, "userTicket", userTicket);

        }

        return user;
    }

}
