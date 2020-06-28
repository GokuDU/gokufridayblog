package com.guo.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guo.vo.PostVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Component
public interface PostMapper extends BaseMapper<Post> {

    IPage<PostVO> selectPosts(Page page,@Param(Constants.WRAPPER) QueryWrapper wrapper);
}
