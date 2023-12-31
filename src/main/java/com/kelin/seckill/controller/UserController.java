package com.kelin.seckill.controller;


import com.kelin.seckill.pojo.User;
import com.kelin.seckill.vo.RespBean;
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
    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user) {
        return RespBean.success(user);
    }
}
