package com.guo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guo.vo.CommentVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Component
public interface CommentMapper extends BaseMapper<Comment> {


    IPage<CommentVO> selectComments(Page page,@Param(Constants.WRAPPER) QueryWrapper<Comment> wrapper);
}
