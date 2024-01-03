package com.kelin.seckill.service;

import com.kelin.seckill.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.vo.GoodsVo;
import com.kelin.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kelin
 * @since 2023-12-23
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);
}
