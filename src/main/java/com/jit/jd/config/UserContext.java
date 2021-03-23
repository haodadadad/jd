package com.jit.jd.config;


import com.jit.jd.pojo.User;


//用户数据存当前线程

public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    public static void setUser(User user){
        userHolder.set(user);
    }

    public static User getUser(){
        return userHolder.get();
    }
}
