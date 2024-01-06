package com.kelin.seckill.controller;


import com.kelin.seckill.pojo.User;
import com.kelin.seckill.rabbitmq.MQSender;
import com.kelin.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author kelin
 * @since 2023-12-18
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }

    @RequestMapping("/mq")
    @ResponseBody
    public void mq() {
        mqSender.send("Hello World!");
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public void mq01() {
        mqSender.send("Hello Fanout!");
    }

    @RequestMapping("/mq/direct01")
    @ResponseBody
    public void mq02() {
        mqSender.send01("Hello direct red.");
    }

    @RequestMapping("/mq/direct02")
    @ResponseBody
    public void mq03() {
        mqSender.send02("Hello direct green.");
    }
}
