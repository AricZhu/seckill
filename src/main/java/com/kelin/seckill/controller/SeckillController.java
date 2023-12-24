package com.kelin.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kelin.seckill.pojo.Order;
import com.kelin.seckill.pojo.SeckillOrder;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IOrderService;
import com.kelin.seckill.service.ISeckillOrderService;
import com.kelin.seckill.vo.GoodsVo;
import com.kelin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    IGoodsService goodsService;

    @Autowired
    ISeckillOrderService seckillOrderService;

    @Autowired
    IOrderService orderService;

    /**
     * 秒杀接口
     * 1. 校验库存
     * 2. 校验用户是否有重复秒杀商品
     */
    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId) {
        if (Objects.isNull(user)) {
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodVoById(goodsId);
        // 1. 判断库存是否不足
        if (goods.getStockCount() < 1) {
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 2. 判断用户是否重复下单
        LambdaQueryWrapper<SeckillOrder> query = new LambdaQueryWrapper<>();
        query.eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goodsId);
        SeckillOrder seckillOrder = seckillOrderService.getOne(query);
        if (!Objects.isNull(seckillOrder)) {
            model.addAttribute("errmsg", RespBeanEnum.REPEAT_SECKILL.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);

        return "orderDetail";
    }
}
