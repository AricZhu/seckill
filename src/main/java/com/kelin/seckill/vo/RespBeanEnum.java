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
    BINDING_ERROR(500003, "参数校验错误"),
    EMPTY_STOCK(500004, "库存不足"),
    REPEAT_SECKILL(500005, "每人限购一件"),
    MOBILE_NOT_EXISTS(500006, "用户不存在"),
    PASSWORD_UPDATE_FAIL(500007, "密码更新失败"),
    ;

    private final Integer code;
    private final String message;
}
