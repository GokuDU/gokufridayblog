package com.guo.service.impl;

import com.guo.entity.UserMessage;
import com.guo.mapper.UserMessageMapper;
import com.guo.service.UserMessageService;
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
public class UserMessageServiceImpl extends ServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

}
