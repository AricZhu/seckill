package com.kelin.seckill.service;

import com.kelin.seckill.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kelin.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kelin
 * @since 2023-12-23
 */
public interface IGoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodVoById(Long goodsId);
}
