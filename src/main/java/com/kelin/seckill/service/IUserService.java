package com.kelin.seckill.service;

import com.kelin.seckill.pojo.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kelin.seckill.vo.LoginVo;
import com.kelin.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author kelin
 * @since 2023-12-18
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo payload, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String ticket);

    RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
