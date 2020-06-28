package com.guo.service.impl;

import com.guo.entity.User;
import com.guo.mapper.UserMapper;
import com.guo.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
