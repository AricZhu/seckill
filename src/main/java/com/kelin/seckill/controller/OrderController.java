package com.kelin.seckill.controller;


import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IOrderService;
import com.kelin.seckill.service.impl.OrderServiceImpl;
import com.kelin.seckill.vo.OrderDetailVo;
import com.kelin.seckill.vo.RespBean;
import com.kelin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author kelin
 * @since 2023-12-23
 */
@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private IOrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId) {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
