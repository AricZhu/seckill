package com.kelin.seckill.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kelin.seckill.pojo.Order;
import com.kelin.seckill.pojo.SeckillOrder;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IOrderService;
import com.kelin.seckill.service.ISeckillOrderService;
import com.kelin.seckill.vo.GoodsVo;
import com.kelin.seckill.vo.RespBean;
import com.kelin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Objects;
import java.util.Random;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀接口
     * 1. 校验库存
     * 2. 校验用户是否有重复秒杀商品
     *
     * 接口压测记录：QPS 507
     * 优化后压测：QPS 1822
     */
    @PostMapping("/doSeckill")
    @ResponseBody
    public RespBean doSeckill(Model model, User user, Long goodsId) throws InterruptedException {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        GoodsVo goods = goodsService.findGoodVoById(goodsId);
        // 1. 判断库存是否不足
        if (goods.getStockCount() < 1) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 2. 判断用户是否重复下单
//        LambdaQueryWrapper<SeckillOrder> query = new LambdaQueryWrapper<>();
//        query.eq(SeckillOrder::getUserId, user.getId()).eq(SeckillOrder::getGoodsId, goodsId);
//        SeckillOrder seckillOrder = seckillOrderService.getOne(query);
        // 使用 redis 来判断是否重复下单
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getId());
        if (!Objects.isNull(seckillOrder)) {
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        Order order = orderService.seckill(user, goods);

        return RespBean.success(order);
    }
}
