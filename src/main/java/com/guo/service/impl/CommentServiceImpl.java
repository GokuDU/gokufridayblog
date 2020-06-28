package com.guo.service.impl;

import com.guo.entity.Comment;
import com.guo.mapper.CommentMapper;
import com.guo.service.CommentService;
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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
