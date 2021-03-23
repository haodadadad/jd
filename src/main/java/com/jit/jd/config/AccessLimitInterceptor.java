package com.jit.jd.config;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jit.jd.pojo.User;
import com.jit.jd.service.IUserService;
import com.jit.jd.utils.CookieUtil;
import com.jit.jd.vo.RespBean;
import com.jit.jd.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

@Component
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception{
        if(handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(accessLimit == null){
                return true;
            }

            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();

            //采用计数器方法，限制访问次数
            String uri = request.getRequestURI();
            if(needLogin){
                if(user == null){
                    render(response, RespBeanEnum.SESSION_ERROR);
                    return false;
                }
                uri += ":" + user.getId();
            }


            ValueOperations valueOperations = redisTemplate.opsForValue();
            Integer count = (Integer) valueOperations.get(uri);
            if(count == null){
                valueOperations.set(uri,1,  second, TimeUnit.SECONDS);

            }else if(count < maxCount){
                valueOperations.increment(uri);
            }else{
                render(response, RespBeanEnum.ACCESS_LIMIT_REAHCED);
                return false;
            }
        }
        return true;
    }

    /*
    获取当前登陆用户
     */
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isBlank(ticket)){
            return null;
        }
        return userService.getUserByCookie(ticket, request, response);
    }

    /*
    构造返回对象
     */
    public void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException{
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        RespBean respBean = RespBean.error(respBeanEnum);
        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
