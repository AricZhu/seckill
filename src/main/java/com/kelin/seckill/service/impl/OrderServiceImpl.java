package com.kelin.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kelin.seckill.exception.GlobalException;
import com.kelin.seckill.pojo.Order;
import com.kelin.seckill.mapper.OrderMapper;
import com.kelin.seckill.pojo.SeckillGoods;
import com.kelin.seckill.pojo.SeckillOrder;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IOrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kelin.seckill.service.ISeckillGoodsService;
import com.kelin.seckill.service.ISeckillOrderService;
import com.kelin.seckill.vo.GoodsVo;
import com.kelin.seckill.vo.OrderDetailVo;
import com.kelin.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kelin
 * @since 2023-12-23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {
    @Autowired
    private ISeckillGoodsService seckillGoodsService;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ISeckillOrderService seckillOrderService;

    @Autowired
    private IGoodsService goodsService;

    /**
     * 1. 商品库存 - 1
     * 2. 创建订单
     * 3. 创建秒杀订单
     */
    @Override
    public Order seckill(User user, GoodsVo goods) {
        // 秒杀商品减库存
        LambdaQueryWrapper<SeckillGoods> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SeckillGoods::getGoodsId, goods.getId());
        SeckillGoods seckillGoods = seckillGoodsService.getOne(queryWrapper);
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsService.updateById(seckillGoods);
        // 创建订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(goods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        // 生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);

        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (orderId == null) {
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodVoById(order.getGoodsId());

        OrderDetailVo detail = new OrderDetailVo();
        detail.setOrder(order);
        detail.setGoodsVo(goodsVo);

        return detail;
    }
}
