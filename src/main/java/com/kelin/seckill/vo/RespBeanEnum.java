package com.kelin.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200, "success"),
    ERROR(500, "服务端异常"),
    LOGIN_ERROR(500001, "用户名或密码错误"),
    MOBILE_ERROR(500002, "手机号格式错误"),
    ;

    private final Integer code;
    private final String message;
}
