package com.guo.service;

import com.guo.common.lang.Result;
import com.guo.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String username, String password);

    void sendMail(User user);
}
