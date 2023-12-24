package com.kelin.seckill.controller;

import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IUserService;
import com.kelin.seckill.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    IGoodsService goodsService;
    @Autowired
    IUserService userService;

    @RequestMapping("/toList")
    public String toList(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        return "goodsList";
    }

    @RequestMapping("/toDetail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable Long goodsId) {
        model.addAttribute("user", user);
        GoodsVo good = goodsService.findGoodVoById(goodsId);
        model.addAttribute("goods", good);

        Date startDate = good.getStartDate();
        Date endDate = good.getEndDate();
        Date nowDate = new Date();

        int secKillStatus = 0;
        int remainSeconds = 0;
        if (nowDate.before(startDate)) {
            secKillStatus = 0;
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
        } else if (nowDate.after(startDate) && nowDate.before(endDate)) {
            secKillStatus = 1;
            remainSeconds = 0;
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
        }

        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goodsDetail";
    }
}
