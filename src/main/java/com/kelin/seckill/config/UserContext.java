package com.kelin.seckill.config;

import com.kelin.seckill.pojo.User;

public class UserContext {
    private static ThreadLocal<User> userHodler = new ThreadLocal<>();

    public static void setUser(User user) {
        userHodler.set(user);
    }

    public static User getUser() {
        return userHodler.get();
    }
}
