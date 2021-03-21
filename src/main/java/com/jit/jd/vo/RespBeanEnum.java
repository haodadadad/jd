package com.jit.jd.vo;



//公共返回对象枚举

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Getter
@ToString
@AllArgsConstructor

public enum RespBeanEnum {
    //通用
    SUCCESS(200,"SUCESS"),
    ERROR(500,"服务端异常"),


    //登录模块

    LOGIN_ERROR(500210,"用户名或者密码不正确"),

    MOBILE_ERROR(500211,"手机号码格式不正确"),

    BIND_ERROR(500212,"参数校验异常");

    private final Integer code;
    private final String message;

}
