package com.guo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guo.entity.Post;
import com.baomidou.mybatisplus.extension.service.IService;
import com.guo.entity.User;
import com.guo.vo.PostVO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author gokufriday
 * @since 2020-06-13
 */
public interface PostService extends IService<Post> {

    IPage<PostVO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVO selectOnePost(QueryWrapper<Object> wrapper);

    void initWeekRank();

    void incrCommentCountAndUnionForWeekRank(long postId,boolean isIncr);

    void putViewCount(PostVO postVO);
}
