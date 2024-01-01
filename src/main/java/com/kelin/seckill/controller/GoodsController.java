package com.kelin.seckill.controller;

import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IGoodsService;
import com.kelin.seckill.service.IUserService;
import com.kelin.seckill.vo.GoodsVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    IGoodsService goodsService;
    @Autowired
    IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 接口压测记录：QPS - 682
     * 优化后（使用 redis 缓存优化）压测记录：QPS - 1753
     */
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
//    @RequestMapping("/toList")
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        // 优化：将页面缓存到 redis 中
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());

        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;

        // 未缓存的代码
//        model.addAttribute("user", user);
//        model.addAttribute("goodsList", goodsService.findGoodsVo());
//        return "goodsList";
    }

    @RequestMapping(value = "/toDetail/{goodsId}",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

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

        WebContext context = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail", html, 60, TimeUnit.SECONDS);
        }

        return html;
    }
}
