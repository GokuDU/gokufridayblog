package com.guo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Post;
import com.guo.mapper.PostMapper;
import com.guo.service.PostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guo.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post> implements PostService {

    @Autowired
    PostMapper postMapper;

    @Override
    public IPage<PostVO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if(level == null)
            level = -1;

        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null,"category_id", categoryId)
                .eq(userId != null,"user_id", userId)   // 当这个id不为空的时候 才有后面的条件
                .eq(level == 0,"level", 0)          // 这里想让数值越大越置顶 ， 默认为0
                .gt(level > 0,"level", 0)       //  level大于0  置顶
                .orderByDesc(order != null,order);      // order是Controller传进来的对应博客表的创建时间 created

        return postMapper.selectPosts(page,wrapper);
    }
}
