package com.kelin.seckill.service.impl;

import com.kelin.seckill.pojo.User;
import com.kelin.seckill.mapper.UserMapper;
import com.kelin.seckill.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

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

}
