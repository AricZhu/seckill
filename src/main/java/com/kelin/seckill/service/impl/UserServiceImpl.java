package com.kelin.seckill.service.impl;

import com.kelin.seckill.exception.GlobalException;
import com.kelin.seckill.pojo.User;
import com.kelin.seckill.mapper.UserMapper;
import com.kelin.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kelin.seckill.utils.CookieUtil;
import com.kelin.seckill.utils.MD5Util;
import com.kelin.seckill.utils.UUIDUtil;
import com.kelin.seckill.vo.LoginVo;
import com.kelin.seckill.vo.RespBean;
import com.kelin.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author kelin
 * @since 2023-12-18
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo payload, HttpServletRequest request, HttpServletResponse response) {
        String mobile = payload.getMobile();
        String password = payload.getPassword();

        User user = userMapper.selectById(mobile);
        if (Objects.isNull(user)) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        // 后端是两次 md5 保存，所以这里对前端上传的密码再进行一次 md5 的计算
        if (!MD5Util.fromPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }

        String ticket = UUIDUtil.uuid();
        redisTemplate.opsForValue().set("user:" + ticket, user);
        // request.getSession().setAttribute(ticket, user);
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String ticket) {
        if (StringUtils.isEmpty(ticket)) {
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + ticket);
        return user;
    }

    @Override
    public RespBean updatePassword(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket);
        if (Objects.isNull(user)) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXISTS);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSlat()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
