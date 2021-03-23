package com.jit.jd.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) //运行时
@Target(ElementType.METHOD) //绑定到方法上面，处理方法
public @interface AccessLimit {
    int second();

    int maxCount();

    boolean needLogin() default true;
}
