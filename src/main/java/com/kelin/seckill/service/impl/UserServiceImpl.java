package com.kelin.seckill.service.impl;

import com.kelin.seckill.pojo.User;
import com.kelin.seckill.mapper.UserMapper;
import com.kelin.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kelin.seckill.utils.MD5Util;
import com.kelin.seckill.utils.ValidatorUtil;
import com.kelin.seckill.vo.LoginVo;
import com.kelin.seckill.vo.RespBean;
import com.kelin.seckill.vo.RespBeanEnum;
import freemarker.template.utility.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Override
    public RespBean doLogin(LoginVo payload) {
        String mobile = payload.getMobile();
        String password = payload.getPassword();

        User user = userMapper.selectById(mobile);
        if (Objects.isNull(user)) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        // 后端是两次 md5 保存，所以这里对前端上传的密码再进行一次 md5 的计算
        if (!MD5Util.fromPassToDBPass(password, user.getSlat()).equals(user.getPassword())) {
            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
        }

        return RespBean.success();
    }
}
