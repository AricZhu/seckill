package com.kelin.seckill.service;

import com.kelin.seckill.pojo.SeckillOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kelin.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kelin
 * @since 2023-12-23
 */
public interface ISeckillOrderService extends IService<SeckillOrder> {

    Long getResult(User user, Long goodsId);
}
