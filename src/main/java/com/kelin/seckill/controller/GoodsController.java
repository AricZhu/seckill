package com.kelin.seckill.controller;

import com.kelin.seckill.pojo.User;
import com.kelin.seckill.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    IUserService userService;

    @RequestMapping("/toList")
    public String toList(Model model, User user) {
        model.addAttribute("user", user);
        return "goodsList";
    }
}
