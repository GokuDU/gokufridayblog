package com.guo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.vo.CommentVO;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
public interface CommentService extends IService<Comment> {


    IPage<CommentVO> paging(Page page, Long postId, Long userId, String order);
}
