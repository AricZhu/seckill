package com.kelin.seckill.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kelin.seckill.config.AccessLimit;
import com.kelin.seckill.exception.GlobalException;
import com.kelin.seckill.pojo.Order;
import com.kelin.seckill.pojo.SeckillMessage;
import com.kelin.seckill.pojo.SeckillOrder;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.rabbitmq.MQSender;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IOrderService;
import com.kelin.seckill.service.ISeckillOrderService;
import com.kelin.seckill.vo.GoodsVo;
import com.kelin.seckill.vo.RespBean;
import com.kelin.seckill.vo.RespBeanEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/seckill")
@Slf4j
public class SeckillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MQSender mqSender;

    private Map<Long, Boolean> emptyStockMap = new HashMap<>();

    /**
     * 秒杀接口
     * 1. 校验库存
     * 2. 校验用户是否有重复秒杀商品
     *
     * 接口压测记录：QPS 507
     * 优化后压测：QPS 1822
     */
    @PostMapping("/{path}/doSeckill")
    @ResponseBody
    public RespBean doSeckill(@PathVariable String path, User user, Long goodsId) throws InterruptedException {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        ValueOperations valueOperations = redisTemplate.opsForValue();

        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if (!Objects.isNull(seckillOrder)) {
            return RespBean.error(RespBeanEnum.REPEAT_SECKILL);
        }
        if (emptyStockMap.get(goodsId)) {
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        // 原子性减1，也可以通过 LUA 脚本来实现原子性操作，关于 LUA 脚本这里不做展开
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if (stock < 0) {
            emptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }

        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.sendSeckillMessage(JSON.toJSONString(seckillMessage));

        return RespBean.success(0);
        /*
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

         */
    }

    /**
     * 初始化时将库存加载到 redis 中
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            emptyStockMap.put(goodsVo.getId(), false);
        });
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId) {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value="/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }

        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    @RequestMapping(value="/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (Objects.isNull(user) || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }

        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300, TimeUnit.SECONDS);

        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败: " + e.getMessage());
        }

    }
}
