package com.guo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.UserMessage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
public interface UserMessageService extends IService<UserMessage> {

    IPage paging(Page page,QueryWrapper<UserMessage> wrapper);
}
